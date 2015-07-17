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
 *  **** TODO: Increase performance and reduce complexity of modal generation by removing use of loading activity indicator, 
 *               and replacing with "pleaseWait" modal overlay.
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
            //errorMessageTitle:"Unable to load the Wait List:",
            //errorMessage:"This may be caused by your session timing out, or a network connection issue. Please login and try again.",
            dateFormat:"dddd m/d/yyyy" 
            
        },
        waitList: {
            width:820,
            height:400,
            title:"Wait List Sign-ups",
            listName:'<i>"[list_name]"</i>',
            listDate:'<i>List Generated on <span class="ft-textGroup">[report_date]</span></i>',
            //errorMessageTitle:"Unable to load the Wait List:",
            //errorMessage:"This may be caused by your session timing out, or a network connection issue. Please login and try again.",
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
            //errorWindowTitle:"Error",
            //errorMessageTitle:"Unable to load page:",
            //errorMessage:"This may be caused by your session timing out, or a network connection issue. Please login and try again.  NOTICE:  If you have mistakenly received this error, and you are using Internet Explorer, please click on the Help? menu item at the top of the ForeTees page for instructions on how to correct this.",
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
            registered:'<b>You are registered for this event.</b>',
            unregistered:'<b>You are not registered for this event.</b>',
            selectButtonInstructions: 'the "Select" button in the table below',
            selectButton:"Select",
            closeButton:"Close"
        //errorWindowTitle:"Error",
        //errorMessageTitle:"Unable to load page:",
        //errorMessage:"This may be caused by your session timing out, or a network connection issue. Please login and try again.  NOTICE:  If you have mistakenly received this error, and you are using Internet Explorer, please click on the Help? menu item at the top of the ForeTees page for instructions on how to correct this."
        },
        eventList: {
            width:800,
            height:400,
            closeButton:"Close"
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
            noResults:["No guests in database:", "Use the form on the left", "to create a new guest."]
        //errorWindowTitle:"Error",
        //errorMessageTitle:"Unable to load page:",
        //errorMessage:"This may be caused by your session timing out, or a network connection issue. Please login and try again.  NOTICE:  If you have mistakenly received this error, and you are using Internet Explorer, please click on the Help? menu item at the top of the ForeTees page for instructions on how to correct this."
        },
        
        guestDbSubmit: {
            width:600,
            height:400,
            loadingTitle:"Please Wait...",
            successTitle:"Guest Registration",
            failTitle:"Guest Registration Error",
            continueButton:"Continue",
            closeButton:"Close"
        //errorWindowTitle:"Error adding guest",
        //errorMessageTitle:"Unable to load page:",
        //errorMessage:"This may be caused by your session timing out, or a network connection issue. Please login and try again.  NOTICE:  If you have mistakenly received this error, and you are using Internet Explorer, please click on the Help? menu item at the top of the ForeTees page for instructions on how to correct this."
        },
        
        slotSubmit: {
            width:650,
            height:300,
            loadingTitle:"Please Wait...",
            continueButton:"Continue",
            closeButton:"Close",
            //errorMessageTitle:"Unable to submit:",
            yesContinueButton:"Yes, Continue",
            noGoBackButton:"No, Go Back"
            
        //errorMessage:"This may be caused by your session timing out, or a network connection issue. Please login and try again."
        },
        
        slotCancelPrompt: {
            width:650,
            height:300,
            title:"Cancel [signup_type] Confirmation",
            continueButton:"Cancel [signup_type]",
            closeButton:"Close",
            promptMessages:[
            '<b>NOTICE:</b> This will remove <b>ALL [options.playerPlural]</b> from this [signup_type].<br>If this is what you want to do, then select "Cancel [signup_type]" below.',
            'If you only want to remove yourself, or a portion of the [options.playerPlural], select "Close" below. Then use the "erase" and "Submit" buttons to remove only those [options.playerPlural] you wish to remove.'             
            ]
        },
        
        slotPageLoadNotification: {
            width:650,
            height:400,
            //title:"Cancel [slot_type] Confirmation",
            continueButton:"Continue",
            acceptButton:"Yes, Continue",
            cancelButton:"Cancel Request",
            goBackButton:"Go Back"

        //errorMessageTitle:"Unable to load page:",
        //errorMessage:"This may be caused by your session timing out, or a network connection issue. Please login and try again.  NOTICE:  If you have mistakenly received this error, and you are using Internet Explorer, please click on the Help? menu item at the top of the ForeTees page for instructions on how to correct this."
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
            title:"Select Members",
            instructions: '<h3>Instructions:</h3><p>To add recipients, select the first letter of the member\'s last name from the table of letters, then click the name in the list on the right.</p>'
            +'<p>When finished, click "[addButton]".</p>',
            instructionsRwd: '<p>To add recipients, select a member using the member selection tool.</p>'
            +'<p>When finished, click "[addButton]".</p>',
            addButton:"Finished",
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
            message2:"This may be caused by network connection issue.  If on a mobile device, please check that your wireless network connection is operational and has good signal strength.",
            tryAgainButton:"Try Again",
            refreshPageButton:'Refresh Page',
            goBackButton:'Go Back',
            closeButton:'Close',
            exitButton:'Exit ForeTees'
            
        },
        
        alertNotice: {
            width:400,
            height:200,
            title:"Alert",
            alertMode: true,
            message_head:"Notice",
            message:"",
            rawMessage:false,
            allowClose:true,
            allowContinue:false,
            closeButton:"Close",
            tryAgainButton:"Try Again",
            refreshPageButton:'Refresh Page',
            goBackButton:'Go Back',
            continueButton: 'Continue',
            continueAction: function(modalObj){
                modalObj.ftDialog("close");
                ft_historyBack();
            },
            onClose: function(){},
            init: function(modalObj){},
            onSuppress: function(){},
            suppressCode: false, // Set to value like "my_message_title" to enable
            suppressPrompt: "Don't show this message again."
        },
        
        help: {
            width:600,
            height:200,
            alertMode: false,
            allowClose: true,
            rawMessage: true,
            closeButton:"Close"
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
    
    var local = {
        getClassHtml: function(ca){
            if(ca && ca.length){
                return ' class="'+ca.join(' ')+'"';
            } else {
                return '';
            }
        },
        /* setInData:function(names,val,data){
            var valuesa = val.split(/\|/g);
            var namesa = names.split(/\|/g);
            //console.log("n/v:"+obj.attr("name")+"!"+obj.val()+"!"+names.length+"!"+values.length);
            if(namesa.length == valuesa.length){
                //console.log("setting values");
                for(var i in namesa){
                    data[namesa[i]] = valuesa[i];
                }
            }else{
                data[names] = val;
            }
        },*/
        setInData:function(names,val,data){
            var valuesa = [], namesa = [];
            if(names != null && typeof names != "undefined"){
                namesa = names.split(/\|/g);
            }
            if(val != null && typeof val != "undefined"){
                valuesa = val.split(/\|/g);
            }
            //console.log("n/v:"+obj.attr("name")+"!"+obj.val()+"!"+names.length+"!"+values.length);
            if(namesa.length == valuesa.length){
                //console.log("setting values");
                for(var i in namesa){
                    data[namesa[i]] = valuesa[i];
                }
            }else{
                data[names] = val;
            }
            
            
        },
        deleteInData:function(names,data){
            var namesa = names.split(/\|/g);
            //console.log("n/v:"+obj.attr("name")+"!"+obj.val()+"!"+names.length+"!"+values.length);
            for(var i in namesa){
                delete data[namesa[i]];
            }
        },
        getTagNames:function(){
            return ftGetTagNames();
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
            var $el = $(this);
            
            // Check if this was just a zoom on a mobile device.  If so, skip the resize
            // We detect by checking if the window aspect ratio is the same
            var currentAspectRatio = Math.round((window.innerHeight / window.innerWidth)*100)/100;
            var lastAspectRatio = 0;
            var timerMs = 20;
            if(ftIsMobile("android")){
                // Android needs a little longer
                timerMs = 500;
            }
            if(typeof($el.data("ft-resizeWindowLastAspectRatio")) != "undefined"){
                lastAspectRatio = $(this).data("ft-resizeWindowLastAspectRatio");
            }
            // console.log("AspectRatio:"+lastAspectRatio+":"+currentAspectRatio);
            if(lastAspectRatio != currentAspectRatio){
                if(typeof($el.data("ft-resizeTimeoutCount")) != "undefined"){
                    timeOutCount = $el.data("ft-resizeTimeoutCount");
                }
                /*
                timeOutCount ++;
                $(this).data("ft-resizeTimeoutCount", timeOutCount);
                setTimeout($.proxy(function(){
                    $(this).foreTeesModal("resizeTimeout");
                },$(this)),timerMs);
                */
                var currentTimer = $el.data("ft-resizeTimeout");
                if(currentTimer){
                    clearTimeout(currentTimer);
                }
                currentTimer = $el.data("ft-resizeTimeout", 
                    setTimeout($.proxy(function(){
                        $el.foreTeesModal("resizeTimeout");
                    },$el),timerMs)
                    );
                
            }
            $el.data("ft-resizeWindowLastAspectRatio", currentAspectRatio);
            //console.log('set-modal-resize-timer');
            
        },
        
        resizeTimeout: function(option){
            
            //console.log('modal-resize-timer');
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
        
        
        bindResize: function(){
            var obj = $(this);
            var option = $(this).data("ft-modalType");
            // Keep the modal centered on resize
            $(window).bind('ft-resize.ft-'+option,function(){
                methods.doResize(obj);
                //obj.foreTeesModal("resize");
            });
            //  Re-center on orientation change on Android
            /*
            if("onorientationchange" in window){
                $(window).bind('orientationchange.ft-'+option,function(){
                    //console.log("change");
                    obj.foreTeesModal("resizeTimer");
                });
            }
            */
            $(window).bind('ftModalCenter.ft-'+option,function(){
                //obj.foreTeesModal("resizeTimer");
                methods.doResize(obj);
            });
            $(window).bind('ftModalForceCenter.ft-'+option,function(){
                //console.log('force resize...');
                //console.log(option);
                //obj.foreTeesModal("resize");
                methods.doResize(obj);
            });
            
        },
        
        doResize: function(obj){
            
            var type = obj.data("ft-modalType");
            
            if(obj.data("ft-modalLastHurrah")){
                obj.data("ft-modalLastHurrah",false);
                obj.foreTeesModal("resize");
            } else if($.inArray(type,["ftS-memberSelect","ftMsPopup","ftPlAddPartner"]) > -1){
                obj.foreTeesModal("resize");
            } else {
                if(pluginData.rft){
                    clearTimeout(pluginData.rft);
                }
                pluginData.rft = setTimeout(function(){
                    $('.ui-dialog:not(.ui-draggable.ui-resizable):visible').not(':hidden').last().find('.ui-dialog-content').foreTeesModal("resize");
                },20);
            }
        },
        
        unbindResize: function(){
            
            var option = $(this).data("ft-modalType");
            $(window).unbind('ft-resize.ft-'+option);
            $(window).unbind('ftModalCenter.ft-'+option);
            $(window).unbind('ftModalForceCenter.ft-'+option);
        /*
            if("onorientationchange" in window){
                $(window).unbind('orientationchange.ft-'+option);
            }
            */
            
        },
        
        resize: function(opt){
            
            //console.log("resizing...");
    
            var o = $(this), $body = $('body');
            var isProshopHybrid = $body.hasClass('proshopHybrid');
            if(!opt){
                opt = o.data("ftMd-resizeOpt");
                if(!opt){
                    opt = {
                        maxWidth:o.data("ft-width"),
                        minHeight:o.data("ft-minHeight"),
                        maxHeight:o.data("ft-minHeight"),
                        scaleHeight:o.data("ft-scaleHeight"),
                        box:o.parent()
                    }
                }
            } else {
                o.data("ftMd-resizeOpt",opt);
            }
            if(!o.data("ft-modalType") && opt.type){
                o.data("ft-modalType",opt.type);
            }
            if(opt.box.css('position') != 'absolute'){
                opt.box.css('width','');
                opt.box.css('height','');
                opt.box.css('top','');
                opt.box.css('left','');
                //opt.box.css("display", '');
                return;
            }
            
            var width = opt.maxWidth;
            var height = opt.minHeight;
            var oldDisplay = opt.box.css("display");
            
            var type = o.data("ft-modalType");

            // check if there is currently a vscroll bar
            var forceHeight = $('<div></div>').css('position','absolute').css('top','0px').css('left','0px').css('width','1px').css('height',(document.documentElement.scrollHeight+1)+'px');
            
            if((document.documentElement.scrollHeight > document.documentElement.clientHeight) && ftIsIe8){
                //console.log('forcing height:'+document.documentElement.scrollHeight+'/'+document.documentElement.clientHeight);
                $body.append(forceHeight);
            }
            
            opt.box.css("display", "none");

            //if(ftIsMobile("ios") || ftIsMobile("android") || ftIsMobile("blackberry")){
            //    var windowWidth = window.innerWidth;
            //    var windowHeight = window.innerHeight;
            //}else{
            var windowWidth = $(window).innerWidth();
            var windowHeight = $(window).innerHeight();
            
            //console.log("windowHeight:"+windowHeight);
            //}   
            
            //var docHeight = $(document).innerHeight();
            var docHeight = ftGetDocHeight();
            //console.log("docHeight:"+docHeight);
            //var bodyHeight = $('body').innerHeight();
            var pageX = $(window).scrollLeft();
            var pageY = $(window).scrollTop();
            var offsetY = 0;
            //var padBody = false;
            
            // Set modal position differently if we are in an iframe
            // and we are able to use postMessage
            if(!isProshopHybrid && parent != window && window.postMessage){
                //padBody = true;
                offsetY = 0-parentSize.offsetTop;
                pageY = (parentSize.scrollTop);
                windowHeight = parentSize.height;
            } else if(!isProshopHybrid && parent != window){
                // If we're in an iframe, but dont support postMessage,
                // Just allow the positioner to resize the iframe, if needed
                //padBody = true;
            }
            
            if(opt.scaleHeight){
                if(windowHeight>height){
                    height = windowHeight;
                }
                if(opt.maxHeight && height > opt.maxHeight){
                    height = opt.maxHeight;
                }
            }
            
            if(opt.box.hasClass("ui-dialog")){
                opt.box.css("display", oldDisplay);
            } else {
                opt.box.css("display", '');
            }
            
            var minWidth = ((windowWidth < width)? windowWidth : width);
            
            
            if(opt.box.hasClass("ui-dialog")){
                o.ftDialog("option",{
                    //position:"center", 
                    width:minWidth, 
                    minHeight:200
                });
            } else {
                opt.box.width(minWidth-2);
                if(opt.scaleHeight){
                    opt.box.height(height-2);
                }
            }
            // Check width again.
            // After setting the modal, the browser may have put up a scrollbar
            // Get the width again, and if the modl size will need to change to fit, 
            // resize the modal.
            
            var newWidth = $(window).innerWidth();
            if(newWidth < windowWidth){
                windowWidth = newWidth;
                var newMinWidth = ((windowWidth < width)? windowWidth : width);
                if(newMinWidth < minWidth){
                    minWidth = newMinWidth;
                    if(opt.box.hasClass("ui-dialog")){
                        o.ftDialog("option",{
                            //position:"center", 
                            width:minWidth, 
                            minHeight:200
                        });
                    }else{
                        opt.box.width(minWidth-2);
                        if(opt.scaleHeight){
                            opt.box.height(height-2);
                        }
                    }
                }
            }
            
            if(($body.hasClass('Accounting') || isProshopHybrid) && opt.box.hasClass("ui-dialog")){
                
                o.ftDialog("option",{
                            //position:"center", 
                            maxHeight:windowHeight
                        });
                
            }
            
            // Center the window
            var boxWidth = (opt.box.outerWidth());
            var boxHeight = (opt.box.outerHeight());

            var top = Math.floor((windowHeight - boxHeight) / 2);

            if((top + boxHeight) > windowHeight){ 
                top = top - ((top + boxHeight) - windowHeight);
            }
            //console.log({docHeight:docHeight,bodyHeight:bodyHeight});
            var newTop = top + pageY + offsetY;
            //if((newTop + boxHeight) > docHeight + iFramePad){
            if((newTop + boxHeight) > docHeight){
                //alert("lower than scroll? ifp:"+iFramePad);
                newTop = (docHeight - boxHeight) - 1;
            }
            if(newTop < 0){
                newTop = 0;
            }
            /*
            if((newTop + boxHeight) > bodyHeight + iFramePad && padBody){
                //console.log('Modal too large for body');
                ftModalPads[type] = ((newTop + boxHeight) - (bodyHeight+iFramePad))+1;
                ftSetMaxIframePad();
            } else {
                ftModalPads[type] = 0;
                ftSetMaxIframePad();
            }
            */
            var left = Math.floor((windowWidth - boxWidth) / 2);
            if((left + boxWidth) > windowWidth){ 
                Math.floor(left = left - ((left + boxWidth) - windowWidth));
            }
            if((left + pageX) < 0){ 
                left = 0;
            }else{
                left += pageX;
            }
            opt.box.offset({
                top: newTop,
                left: left
            });
            forceHeight.remove();
            ftSizeCheck();
            /*
            if(o.data('ft-postResize')){
                o.data('ft-postResize')();
            }
            */
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
            var modalObj;
            if (typeof(obj.data("ft-"+option+"Obj")) != "object"){
                modalObj = $('<div class="modal_list '+option+'_container"></div>');
                $("body").append(modalObj);
                obj.data("ft-"+option+"Obj", modalObj);
            } else {
                modalObj = obj.data("ft-"+option+"Obj");
            }
            modalObj.empty();
            modalObj.data("ft-modalParent",obj);
            modalObj.data("ft-modalType",option);
            
            return modalObj;
            
        },
        
        initModalObject: function (title, option_override){
            
            ftUnHoverMenu();
            
            var modalObj = $(this);
            //if(!option_override){
            //    option_override = {};
            //}

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
            //var width = modalObj.data("ft-width");
            //var height = modalObj.data("ft-height");
            var minHeight = (($(window).innerHeight() < height)? $(window).innerHeight() : height);
            var minWidth = (($(window).innerWidth() < width+10)? $(window).innerWidth()-10 : width);
            //var maxHeight = $(window).innerHeight();
            //var maxWidth = $(window).innerWidth();
            
            
            modalObj.ftDialog({
                resizable: false,
                draggable: false,
                modal:true,
                width:minWidth,
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
                                    //ftModal.foreTeesModal("triggerResize");
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
                    
                    setTimeout(function(){
                        $(window).trigger('resize');
                    },50);
                    
                },
                close:function(){
                    
                    var obj = $(this);
                    // Stop the activity indicator
                    delete ftModalPads[$(this).data("ft-modalType")];
                    obj.data('didFocus',false);
                    obj.foreTeesModal("triggerEvent","onClose");
                    //$(window).trigger("ftModalCenter");
                    obj.foreTeesModal("hideActivity");
                    obj.data("ft-modalLastHurrah",true);
                    $(window).trigger('resize');
                    obj.foreTeesModal("unbindResize");
                    //ftSetMaxIframePad();
                    //$(window).trigger('resize');
                    //$('.ui-widget-overlay').click();
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
        
        
        bindForm: function(formObj, data){
            
            // Bind to changable elements
            var formElem = formObj.find("select,input,textarea");
            //formElem.data("ft-formData", data);
            formElem.each(function(){
                $(this).change(function(){
                    var obj = $(this);
                    //var formData = obj.data("ft-formData");
                    // A form element may conatin multiple data parameters, delimited by a "|";
                    var val = obj.val();
                    var name = obj.attr("name");
                    //console.log("n/v:"+obj.attr("name")+"!"+obj.val()+"!"+names.length+"!"+values.length);
                    if(obj.is(":radio") && obj.is(":checkbox")){
                        val = obj.closest("form").find(obj.prop('tagName')+'[name='+name+']:checked').val();
                    }
                    local.setInData(name, val, data);
                });
                $(this).change(); // trigger change event to set default in data object
            });
            // Bind to clickable elements
            formElem = formObj.find('button, input[type="button"]');
            //formElem.data("ft-formData", data);
            formElem.each(function(){
                $(this).click(function(e){
                    var obj = $(this);
                    //var formData = obj.data("ft-formData");
                    // A form element may conatin multiple data parameters, delimited by a "|";
                    local.setInData(obj.attr("name"), obj.val(), data);
                    e.preventDefault();
                //return false;
                });
                $(this).change(); // trigger change event to set default in data object
            });
            
            return formObj;
        },
        
        buildForm: function(fields, data, parms, reqFieldColor){
            var formObj = $("<form></form>");
            formObj.data("ft-resubmit-page", function(context){
                
                });
            var current_container = formObj;
            var current_block = formObj;
            var last_container = formObj;
            //var last_block = formObj;
            var records = [], record, key, object;
            if(!$.isArray(fields)){
                for(key in fields){
                    record = fields[key];
                    record.key = key;
                    records.push(record);
                }
            } else {
                records = fields;
            }
            for(var r = 0; r < records.length; r++){
                
                record = records[r];
                key = record.key;
                if(typeof key == "undefined"){
                    key = 'formelement_'+r;
                }
                var type_a = ['unknown','unknown'];
                if(record.type){
                    type_a = record.type.split(/_/g);
                }
                var type = type_a[0];
                var disabled = "";
                
                //var force_store = false;
                if(type_a[1] == "disabled"){
                    disabled =  'disabled="disabled" ';
                //force_store = true;
                }
                var submit_name = key;
                if(record.submit_name && record.submit_name.length){
                    submit_name = record.submit_name;
                }
                var update_target = false;
                var label_text = "";
                if(record.label){
                    label_text = ftReplaceKeyInString(record.label,parms);
                }
                var label = '';
                if(label_text.length){
                    label = '<label for="'+key+'" onclick=\"\">' + label_text + ':</label>';
                }
                var value = record["value"];
                //console.log(key+":"+type+":"+value);
                
                var field = "";
                var classa = [];
                var maxlen = "";
                var size = "";
                var checked = false;
                var required = "";
                var max_date = "";
                var min_date = "";
                var default_date = "";
                var appendLabel = false;
                var prependLabel = false;
                var raw = "";
                var fclass = record["class"];
                var style = "";
                //console.log("Field:"+key+";value:"+value);
                if(fclass && fclass.length > 0){
                    classa.push(record["class"]); 
                } else {
                    fclass = "";
                }
                var classb = classa.slice(0);
                classb.push("field_container");
                if(record.required){
                    required = " <span class=\"require_notice\">*</span>";
                    classb.push("ftRequired");
                    if(reqFieldColor){
                        style = ' style="background-color:'+reqFieldColor+'"';
                    }
                    
                }
                var sol = '<p'+local.getClassHtml(classb)+'>';
                var eol = "</p>";
                if(record.maxlen > 0){
                    maxlen = ' maxlength="'+record.maxlen+'"'; 
                }
                if(record.max_date){
                    max_date = ' data-ftenddate="'+record.max_date+'"'; 
                }
                if(record.min_date){
                    min_date = ' data-ftstartdate="'+record.min_date+'"'; 
                }
                if(record.min_date){
                    default_date = ' data-ftdefaultdate="'+record.default_date+'"'; 
                }
                if(record.size > 0){
                    size = ' size="'+record.size+'"'; 
                }
                if((typeof record.checked == "undefined" && record.value == "1")){
                    checked = true;
                } else if(typeof record.checked != "undefined"){
                    checked = record.checked;
                }
                
                switch(type){
                    
                    case "fieldset":
                        object = $('<fieldset'+local.getClassHtml(classa)+'></fieldset>');
                        current_block.append(object);
                        if(!last_container.is('fieldset')){
                            last_container = current_container;
                        }
                        current_container = object;
                        if(label_text.length){
                            current_container.append('<ledgend>'+label_text+'</ledgend>');
                        }
                        break;
                       
                    case "end-fieldset":
                        if(!last_container.is('fieldset')){
                            last_container = current_container;
                        }
                        current_container = formObj;
                        break;
                        
                    case "radioblock":
                        classa.push('ftRadioSelectBlock');
                        object = $('<div data-ftblocktype="radioblock" '+local.getClassHtml(classa)+' data-radioblocktname="'+submit_name+'"></div>');
                        if(label_text.length){
                            object.append('<h3>'+label_text+'</h3>');
                        }
                        current_block.append(object);
                        if(!last_container.is('fieldset')){
                            last_container = current_container;
                        }
                        current_container = object;
                        break;
                      
                    case "radioselect":
                        if(current_container.is('[data-ftblocktype="radioblock"]')){
                            if(!last_container.is('[data-ftblocktype="radioblock"]')){
                                current_container = last_container;
                            } else {
                                current_container = formObj;
                            }
                        }
                        classa.push('ftRadioSelectInput');
                        field = $('<input '+disabled+'type="radio" name="'+submit_name+'" data-ftsubmitname="'+submit_name+'" value="'+value+'" '+size+''+maxlen+''+local.getClassHtml(classa)+'>');
                        field.prop('checked',checked);
                        sol = '<div class="ftRadioSelect '+fclass+'" data-ftname="'+submit_name+'"><label onclick="">';
                        eol = '<span>'+label_text+'</span></label></div>';
                        label = '';
                        field.on('ftclear', function(submit_name){
                            return function(e){
                                $(this).closest('form').find('.ftRadioSelect[data-ftname="'+submit_name+'"]').addClass("inactive").removeClass("active").each(function(){
                                    $(this).next().addClass("inactive").removeClass("active").find('input,select,textarea').each(function(){
                                        //delete data[$(this).attr('data-ftsubmitname')];
                                        local.deleteInData($(this).attr("data-ftsubmitname"), data);
                                    });
                                    ftSizeCheck();
                                });
                            }
                        }(submit_name));
                        field.on('ftactive', function(submit_name){
                            return function(e){
                                var el = $(this);
                                if(el.is(':checked')){
                                    el.closest('.ftRadioSelect[data-ftname="'+submit_name+'"]').addClass("active").removeClass("inactive").each(function(){
                                        $(this).next().addClass("active").removeClass("inactive").find('input,select,textarea').each(function(){
                                            local.setInData($(this).attr("data-ftsubmitname"), $(this).val(), data);
                                        });
                                    }); 
                                //console.log("Checked:" + el.val());
                                } //else {
                            //console.log("Not Checked:" + el.val());
                            //}
                            //console.log(data);
                                ftSizeCheck();
                            }
                        }(submit_name));
                        prependLabel = true;
                        break;
                        
                    case "date":
                        classa.push('ft_date_picker_field');
                        field = $('<input '+disabled+'type="text" name="'+key+'" data-ftsubmitname="'+submit_name+'" value="'+value+'" '+min_date+max_date+default_date+' '+local.getClassHtml(classa)+''+style+' readonly="true" tabindex="-1">');
                        sol = '<p class="field_container ftDateSelect" data-ftname="'+submit_name+'"><label onclick=""><span>'+label_text+'</span>';
                        eol = '</label></p>';
                        appendLabel = true;
                        label = '';
                        break;
                        
                    case "html":
                        raw = record.label
                        field = '';
                        break;
                    
                    case "end-radioblock":
                        if(!last_container.is('[data-ftblocktype="radioblock"]')){
                            current_container = last_container;
                        } else {
                            current_container = formObj;
                        }
                        break;
                        
                    case "fieldblock":
                        classa.push('ftFieldBlock');
                        object = $('<div'+local.getClassHtml(classa)+'></div>');
                        if(label_text.length){
                            object.append('<h3>'+label_text+'</h3>');
                        }
                        formObj.append(object);
                        current_block = object;
                        current_container = object;
                        break;
                        
                    case "end-fieldblock":
                        current_container = formObj;
                        current_block = formObj
                        break;
                        
                    case "messageblock":
                        object = $('<div'+local.getClassHtml(classa)+'><h4>'+label_text+'</h4>'+ftReplaceKeyInString(value,parms)+'</div>');
                        current_block.append(object);
                        break;
                    
                    case "hidden":
                        //force_store = true;
                        label = "";
                        sol = "";
                        required = "";
                        eol = "";
                        field = '<input type="hidden" name="'+key+'" data-ftsubmitname="'+submit_name+'" value="'+value+'">';
                        break;
                        
                    case "display":
                        //force_store = true;
                        field = '<input disabled="disabled" type="text" name="'+key+'" data-ftsubmitname="'+submit_name+'" value="'+value+'"'+size+''+maxlen+''+local.getClassHtml(classa)+'>';
                        break;
                                        
                    case "text":
                        field = '<input '+disabled+'type="text" name="'+key+'" data-ftsubmitname="'+submit_name+'" value="'+value+'"'+size+''+maxlen+''+local.getClassHtml(classa)+''+style+'>';
                        break;
                                        
                    case "checkbox":
                        field = $('<input '+disabled+'type="checkbox" name="'+key+'" data-ftsubmitname="'+submit_name+'" value="'+value+'"'+local.getClassHtml(classa)+'>');
                        field.prop('checked',checked);
                        break;
                                        
                    case "select":
                        field = $('<select '+disabled+'name="'+key+'" data-ftsubmitname="'+submit_name+'"'+local.getClassHtml(classa)+''+style+'></select>');
                        for(var oi=0; oi < record.options.length; oi++){
                            //for(var option_name in record.options){
                            var orec = record.options[oi];
                            var option_name = orec.text;
                            var option_value = orec.value;
                            var option_target = false;
                            if(typeof option_value == "object"){
                                option_target = option_value;
                                option_value = option_target.value;
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
                        //if(!force_store){
                        if(record.submit_on_change){
                            field[0].ftSOC = record.submit_on_change;
                        }
                        field[0].ftLastVal = value;
                        //fieldObj.data("ft-formData", data);
                        fieldObj.on('change',function(e){
                            //e.preventDefault();
                            var el = $(this);
                            var sb = el.closest('.ftRadioSelectBlock');
                            if(!el.is('.ftRadioSelectInput') && sb.length && !sb.prev().find('.ftRadioSelectInput').is(':checked')){
                                return;
                            }
                            var ename = el.attr("data-ftsubmitname");
                            //var formData = obj.data("ft-formData");
                            // A form element may conatin multiple data parameters, delimited by a "|";
                            if(el.is('input[type="radio"],input[type="checkbox"]')){
                                var selobj = formObj.find('input[name="'+ename+'"]:checked').first();
                                if(selobj.length){
                                    selobj.trigger('ftclear').trigger('ftactive');
                                    local.setInData(ename, selobj.val(), data);
                                } else {
                                    local.deleteInData(ename, data);
                                }
                            } else {
                                local.setInData(ename, el.val(), data);
                            }
                                
                            if(el[0].ftLastVal != el.val() && typeof el[0].ftSOC == "string" && typeof parms.soc == 'function'){
                                local.setInData(el[0].ftSOC, el[0].ftSOC, data);
                                parms.soc(el);
                            }
                            el[0].ftLastVal = el.val();
                                
                        //console.log(data);
                                
                        });
                    //fieldObj.trigger('change'); // trigger change event to set default in data object
                    //}else{
                    //     data[key] = value; // set default in data object
                    //}
                        
                    }
                    if(update_target){
                        fieldObj.on('change',function(){
                            var el = $(this);
                            //console.log("trigger:"+obj.attr('name'));
                            var option_obj = el.find('option:selected');
                            //console.log("option:"+option_obj.val());
                            var target_data = option_obj.data("ft-changeTarget");
                            var target_obj = el.parents('form').first().find('select[name="'+target_data['name']+'"]');
                            if(!target_obj.length){
                                target_obj = el.parents('form').first().find('select[data-ftsubmitname="'+target_data['name']+'"]');
                            }
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
                                    option_obj = $('<option value="'+option_value+'"'+selected+'>'+option_name+'</option>');
                                    target_obj.append(option_obj);
                                    target_obj.change();
                                }
                            }
                        });
                    }
                    if(appendLabel){
                        parmObj.find('label').append(fieldObj);
                    } else if(prependLabel){
                        parmObj.find('label').prepend(fieldObj);
                    } else {
                        parmObj.append(fieldObj);
                    }
                    parmObj.append(required);
                    current_container.append(parmObj);
                    fieldObj.trigger('change');
                    fieldObj.closest('div[data-ftblocktype="radioblock"]').closest('p').prev().find('input.ftRadioSelect');
                } else {
                    current_container.append(raw); 
                }
            }
            formObj.find('.ftRadioSelectInput').trigger('ftclear').trigger('ftactive');
            return formObj;  
        },
        
        
          
        htmlBlock: function (new_option, obj) {
            
            var option = {};
            
            if(typeof new_option == "object"){
                option = $.extend({}, options.htmlBlock, new_option);
            } else {
                option = $.extend({}, options.htmlBlock);
                option.action = new_option;
            }
            if(!obj){
                obj = $(this);
            }
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
                        error: function(xhr, e_text, e) {
                            methods.ajaxError({
                                title:"Error opening window.  Please try again later.",
                                jqXHR: xhr, 
                                textStatus: e_text, 
                                errorThrown: e, 
                                allowBack: true,
                                allowRefresh: false,
                                tryAgain: function() {
                                    methods.htmlBlock(new_option, obj);
                                }
                            },obj);
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
        
        help: function (new_option, obj) {
            if(!obj){
                obj = $(this);
            }
            
            var option = {};
            
            if(typeof new_option == "object"){
                option = $.extend({}, options.help, new_option);
            } else {
                option = $.extend({}, options.help);
                option.helpFile = new_option;
            }

            $('body').foreTeesModal('pleaseWait');
            
            var url;
            if(option.fullPath){
                url = option.fullPath;
            } else {
                url = '../assets/help/'+option.helpFile+'.html';
            }

            $.ajax({
                type: 'GET',
                url: url,
                dataType: 'text',
                success: function(data, textStatus, jqXHR){
                    $('body').foreTeesModal('pleaseWait','close');
                    option.title = ftExtractValue(data, /<title[^>]*>([^<]*)<\/title[^>]*>/i, "");
                    if(!option.title){
                        option.title = option.altTitle ;
                    }
                    option.message = data.replace(/<title[^>]*>((?:[\s\S](?!<\/title))*)[^<]*<\/title[^>]*>/ig, "");
                    methods.alertNotice(option, obj);
                },
                error: function(xhr, e_text, e) {
                    $('body').foreTeesModal('pleaseWait','close');
                    //ft_logError("Unable to load file:", xhr);
                    methods.ajaxError({
                        jqXHR: xhr, 
                        textStatus: e_text, 
                        errorThrown: e, 
                        allowBack: false,
                        allowRefresh: false,
                        allowClose: true,
                        title:"Unable to load file",
                        message1:'<p>Unable to load file "'+option.helpFile+'"</p>',
                        message2:'<p>Please tell us about this so we can fix it.</p>',
                        tryAgain: function() {
                            methods.help(new_option, obj);
                        }
                    },obj);
                }
            });
        },
        
        alertNotice: function (new_option, obj) {
            if(!obj){
                obj = $(this);
            }
            var option = {};
            
            if(typeof new_option == "object"){
                option = $.extend({}, options.alertNotice, new_option);
            } else {
                option = $.extend({}, options.alertNotice);
                option.action = new_option;
            }
            
            var user = $.fn.foreTeesSession('get','user');
            var club = $.fn.foreTeesSession('get','club');
            var sc = option.suppressCode;
            var scc = 'ftspra_' + user + '_' + club + '_' + sc;
            if(sc && localStorage.getItem(scc) == "true"){
                // user has requested that this alert be suppressed
                option.onSuppress();
                return;
            }
            
            obj.data("ft-alertNoticeModalData", option);
            var modalObj = obj.foreTeesModal("getModalObject","alertNotice");
            
            option.options = options;
            
            switch (option.action) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(option.title, option), option);
                    //modalObj.ftDialog("open");
                    modalObj.empty();
                    
                    var message = "";
                    if(option.rawMessage){
                        message = option.message;
                    } else if(option.message || (option.message_head && option.alertMode)) {
                        if(option.alertMode){
                            var message_ext = ftReplaceKeyInString(option.message, option);
                            if(message_ext.length > 0){
                                message_ext = '<br><p>'+message_ext+'</p>'
                            }
                            message += '<div class="sub_instructions"><p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>'+ftReplaceKeyInString(option.message_head, option)+'</p>'+message_ext+'</div>';
                        } else {
                            message += '<div class="main_instructions">'+ftReplaceKeyInString(option.message, option)+'</div>' 
                        }
                    }
                    
                    //var messageObj = $(message);
                    if(message){
                        modalObj.append(message);
                    }

                    if (option.messageObject) {
                        modalObj.append(option.messageObject);
                    }
                    
                    if(sc){
                        var noDisp = $('<div class="sub_instructions"><label><input type="checkbox" name="suppressAlert" value="true">'+option.suppressPrompt+'</label></div>');
                        noDisp.find("input").change(function(){
                            var o = $(this);
                            if(o.prop("checked")){
                                localStorage.setItem(scc, "true");
                            } else {
                                localStorage.setItem(scc, "false");
                            }
                        });
                        modalObj.append(noDisp);
                    }
                    
                    if(option.form_list && option.form_list.length){
                        var formContainer = $('<div class="main_instructions field_list_container"></div>');
                        modalObj.append(formContainer);
                        formContainer.append(obj.foreTeesModal("buildForm", option.form_list, option.form_data, {}));
                    }
                    
                    var buttons = {};

                    if(typeof option.tryAgain == "function"){
                        buttons[ftReplaceKeyInString(option.tryAgainButton, option)] = function(){
                            var obj = $(this).data("ft-modalParent");
                            option = obj.data("ft-alertNoticeModalData");
                            $(this).ftDialog("close");
                            option.tryAgain(option);
                        }
                    }
                    
                    if(option.reloadButton && typeof option.reloadAction == "function"){
                        buttons[ftReplaceKeyInString(option.reloadButton, option)] = function(){
                            var obj = $(this).data("ft-modalParent");
                            option = obj.data("ft-alertNoticeModalData");
                            option.reloadAction($(this), option);
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
                        beforeClose: option.onClose,
                        buttons: buttons

                    });
                    ftActivateElements(modalObj);
                    option.init(modalObj);
                    modalObj.ftDialog("open");
                    if(option.allowClose){
                        modalObj.foreTeesModal("hideActivity");
                    } else {
                        modalObj.foreTeesModal("hideActivityOnly");
                    }
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
   
        ajaxError: function (new_option, obj) {
            
            var option = {};
            
            if(typeof new_option == "object"){
                option = $.extend({}, options.ajaxError, new_option);
            } else {
                option = $.extend({}, options.ajaxError);
                option.mode = new_option;
            }
            
            if(!obj){
                obj = $(this);
            }
 
            //obj.data("ft-ajaxErrorModalData", option);
            var modalObj = obj.foreTeesModal("getModalObject","ajaxError");
            
            var action = option.action;
            if(action == null){
                action = "open";
            }
            
            //option.modalObj = modalObj;
            
            if(option.jqXHR && option.jqXHR.responseText && 
                (
                option.jqXHR.responseText.indexOf('Access Error - Please Read') > -1
                || option.jqXHR.responseText.indexOf('your session has either expired or was not established correctly') > -1
            )){
                option.title = "Session Timed Out";
                option.message1 = "Sorry, your session has timed out, or you have otherwise been logged out of ForeTees."
                option.message2 = "You must exit ForeTees and try again."
                option.tryAgain = null;
                option.allowRefresh = false;
                option.allowBack = false;
                option.allowExit = true;
                if(option.slotParms){
                    option.slotParms.skip_unload = true;
                }
            } else if (option.errLog) {
                ft_logError(option.errLog, option.jqXHR);
            } else if (option.title) {
                ft_logError(option.title, option.jqXHR);
            } else {
                ft_logError("Unknown method source", option.jqXHR);
            }

            switch (action) {
                default:
                case "open":
                    $('body').foreTeesModal("pleaseWait","close");
                    obj.foreTeesModal("pleaseWait","close");
                    modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(option.title, option));
                    modalObj.ftDialog("open");
                    modalObj.empty();
                    modalObj.foreTeesModal("hideActivityOnly");

                    var message = "";
                    message += '<div class="sub_instructions"><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>'+ftReplaceKeyInString(option.message1, option)+'</div><div class="sub_instructions">'+ftReplaceKeyInString(option.message2, option)+'</div>';
                    
                    modalObj.append(message);

                    var buttons = {};

                    if(typeof option.tryAgain == "function"){
                        buttons[ftReplaceKeyInString(option.tryAgainButton, option)] = function(){
                            //var obj = $(this).data("ft-modalParent");
                            //option = obj.data("ft-ajaxErrorModalData");
                            modalObj.ftDialog("close");
                            option.tryAgain(new_option);
                        }
                    }
                    
                    if(option.allowClose){
                        buttons[ftReplaceKeyInString(option.closeButton, option)] = function(){
                            modalObj.ftDialog("close");
                            if(typeof option.closeAction == "function"){
                                option.closeAction();
                            }
                        }
                    }
                    
                    if(option.allowExit){
                        var url = $('a[href^="Logout?"]').attr('href');
                        if(!url){
                            url = "Logout"
                        }
                        // Show exit foretees button if we have a logout url
                        buttons[ftReplaceKeyInString(option.exitButton, option)] = function(){
                            obj.foreTeesModal("pleaseWait");  
                            window.location.href = url;
                        }
                    }
                    
                    if(option.allowRefresh){
                        buttons[ftReplaceKeyInString(option.refreshPageButton, option)] = function(){
                            //var obj = $(this).data("ft-modalParent");
                            obj.foreTeesModal("pleaseWait");  
                            window.location.reload();
                        }
                    }
                    
                    if(option.allowBack && document.referrer != ""){
                        buttons[ftReplaceKeyInString(option.goBackButton, option)] = function(){
                            //var obj = $(this).data("ft-modalParent");
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
        
        getMemberDistributionList: function (new_option, obj) {
            
            var option = {};
            
            if(typeof new_option == "object"){
                option = $.extend({}, options.getMemberDistributionList, new_option);
            } else {
                option = $.extend({}, options.getMemberDistributionList);
                option.mode = new_option;
            }
            if(!obj){
                obj = $(this);
            }
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
                            html += '<div class="main_instructions pageHelp" data-fthelptitle="Instructions">'
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
                                    var listObj = $('<div><p><input type="checkbox" name="list_'+ i +'" value="'+data[i]+'"><label  onclick="" for="list_'+ i +'">'+data[i]+'</label></p></div>');
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
                                
                                var url = "data_loader?json_mode=true&email&dist_lists="+encodeURIComponent(JSON.stringify(lists));
                                
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
                                        error: function(xhr, e_text, e) {
                                            obj.foreTeesModal("pleaseWait", "close");
                                            ft_logError("Error loading email distributions list", xhr);
                                            obj.foreTeesModal("ajaxError", {
                                                jqXHR: xhr, 
                                                textStatus: e_text, 
                                                errorThrown: e, 
                                                allowClose: true
                                            });
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
                            ftActivateElements(modalObj);
                            modalObj.foreTeesModal("triggerResize");

                            
                            
                        },
                        error: function(xhr, e_text, e) {
                            obj.foreTeesModal("ajaxError", {
                                jqXHR: xhr, 
                                textStatus: e_text, 
                                errorThrown: e, 
                                allowBack: true,
                                allowRefresh: false,
                                logErr: "Error loading email distribution list names",
                                tryAgain: function() {
                                    methods.getMemberDistributionList(new_option, obj);
                                }
                            });
                        }
                    });
                    break;
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
            
        },
        
        getMemberRecipients: function (new_option, obj) {
            
            var option = {};
            var rwd = $.fn.foreTeesSession('get','rwd');
            
            if(typeof new_option == "object"){
                option = $.extend({}, options.getMemberRecipients, new_option);
            } else {
                option = $.extend({}, options.getMemberRecipients);
                option.mode = new_option;
            }
            if(!obj){
                obj = $(this);
            }
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
                    html += '<div class="name_list_container rwdCompact">'
                    if(rwd){
                        html += '<div class="ftMs-pageOverlay"></div>';
                    }
                    html += '<div class="widget_container_right">';
                    if(rwd){
                        // Responsive member select
                        html += '<div class="memberSelectContainer"><div class="memberSelectPrompt"><a class="standard_button" href="#">Close</a><div>Add Recipient</div></div></div>';
                    } else {
                        // Desktop member select
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
                    }
                    html += '</div>';
                    
                    html += '<div class="widget_container_fill">'
                    html += '<div class="main_instructions pageHelp" data-fthelptitle="Instructions">'
                    if(rwd){
                        html += ftReplaceKeyInString("[instructionsRwd]", option);
                    } else {
                        html += ftReplaceKeyInString("[instructions]", option);
                    }
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
                    var dfn = "ft_defaultName";
                    htmlObj.find('input[name=distribution_list_name]')
                    .focus(function(){
                        var el = $(this);
                        if(option.baseName && el.val().indexOf(option.baseName) == 0){
                            // It's a "new list" name
                            el.data(dfn,el.val());
                            el.val("");
                        }
                    })
                    .blur(function(){
                        var el = $(this);
                        if(!el.val().length && el.data(dfn)){
                            el.val(el.data(dfn));
                        }
                    });
                    var listContainer = htmlObj.find(".reciptient_container"); 
                    parms.addRecipientsObj = $('<div></div>');
                    listContainer.append(parms.addRecipientsObj);
                    
                    modalObj.append(htmlObj);
                    
                    parms.recipients = {};
                    
                    if(typeof option.listName == "string" && option.listName.length > 0){
                        
                        var url = "data_loader?json_mode=true&email&dist_lists="+encodeURIComponent(JSON.stringify([option.listName]));  
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
                            error: function(xhr, e_text, e) {
                                obj.foreTeesModal("pleaseWait", "close");
                                methods.ajaxError({
                                    title:"Unable to load distribution list",
                                    jqXHR: xhr, 
                                    textStatus: e_text, 
                                    errorThrown: e,
                                    allowBack: true,
                                    tryAgain: function() {
                                        methods.getMemberRecipients(new_option, obj);
                                    }
                                },obj);
                            }
                        });
                    }
                    
                    if(rwd){
                        // Responsive member select
                        var rwdSc = htmlObj.find(".memberSelectContainer");
                        rwdSc.foreTeesMemberSelect("init",{
                            partnerSelect: true,
                            memberSearch: true,
                            requireEmail: true,
                            onSelect:function(record, o){
                                htmlObj.removeClass('toolsOpen');
                                modalObj.foreTeesEmail("addRecipient", record);
                            }
                        });
                        listContainer.append('<div class="rwdAddRecipient"><a href="#" class="button_add_icon">Add Recipient</a></div>');
                        listContainer.find('.rwdAddRecipient a').click(function(e){
                            htmlObj.addClass('toolsOpen');
                            e.preventDefault();
                        });
                        rwdSc.find('.memberSelectPrompt .standard_button').click(function(e){
                            htmlObj.removeClass('toolsOpen');
                            e.preventDefault();
                        });
                    } else {
                        // Desktop memebr select
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

                    }

                    var buttons = {};

                    buttons[ftReplaceKeyInString(option.cancelButton, option)] = function(){
                        $(this).ftDialog("close");
                    }
                    
                    buttons[ftReplaceKeyInString(option.addButton, option)] = option.callbackAction;

                    modalObj.ftDialog("option",{
                        buttons: buttons
                    });
                    
                    ftActivateElements(modalObj);
                    
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
            var slotParms = obj.data("ft-slotParms");
            if(slotParms.skip_unload){
                return;
            }
            var modalObj = obj.foreTeesModal("getModalObject","slotTimeout");
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
                        
                        //var obj = $(this).data("ft-modalParent");
                        //var slotParms = obj.data("ft-slotParms");
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
            
            var obj = $(this),
                modalObj = obj.foreTeesModal("getModalObject","slotPageLoadNotification"), 
                slotParms = obj.data("ft-slotParms"),
                $body = $('body');
            $body.foreTeesModal("pleaseWait","close");
            
            var i, formObj;
            switch (option) {
                default:
                case "open":
                    $body.addClass('ftSPLOpen');
                    modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(slotParms.page_start_title, slotParms));
                    modalObj.ftDialog("open");
                    modalObj.empty();
                    modalObj.foreTeesModal("hideActivityOnly");
                    
                    var message = "";
                    
                    if(slotParms.page_start_instructions.length > 0){
                        if(slotParms.callback_form_html.length > 0 || ftCount(slotParms.callback_form_map) > 0){
                            message += '<div class="main_instructions pageHelp" data-fthelptitle="Instructions">';
                        }else{
                            message += '<div class="main_instructions">';
                        }
                        for (i = 0; i < slotParms.page_start_instructions.length; i ++ ){
                            if(i>0){
                                message += '<br><br>';
                            }
                            message +=  ftReplaceKeyInString(slotParms.page_start_instructions[i], slotParms);
                        }
                        message += '</div>';
                    }
                    
                    for (i = 0; i < slotParms.page_start_messages.length; i ++ ){
                        message += '<div class="sub_instructions ftClubMessage">';
                        message +=  ftReplaceKeyInString(slotParms.page_start_messages[i], slotParms);
                        message += '</div>';
                    }

                    if(slotParms.page_start_notifications.length > 0){
                        message += '<div class="sub_instructions">';
                        for (i = 0; i < slotParms.page_start_notifications.length; i ++ ){
                            if(i>0){
                                message += '<br><br>';
                            }
                            message +=  ftReplaceKeyInString(slotParms.page_start_notifications[i], slotParms);
                        }
                        message += '</div>';
                    }

                    if(slotParms.page_start_htmlblocks.length > 0){
                        for (i = 0; i < slotParms.page_start_htmlblocks.length; i ++ ){
                            message += ftReplaceKeyInString(slotParms.page_start_htmlblocks[i], slotParms);
                        }
                    }
                    
                    modalObj.append(message);
                    
                    slotParms.soc = function(){
                        $body.foreTeesModal("pleaseWait");
                        modalObj.ftDialog("close");
                        obj.foreTeesSlot("refreshSlot");
                    }
                    
                    if(slotParms.callback_form_html.length > 0){
                        //console.log("binding form");
                        formObj = $('<div class="main_instructions"><form>'+ftReplaceKeyInString(slotParms.callback_form_html, slotParms)+'</form></div>');
                        modalObj.append(obj.foreTeesModal("bindForm", formObj, slotParms.callback_map));
                    }           
                    
                    if(slotParms.alt_callback_form_html.length > 0){
                        formObj = $('<div class="main_instructions"><form>'+ftReplaceKeyInString(slotParms.alt_callback_form_html, slotParms)+'</form></div>');
                        modalObj.append(obj.foreTeesModal("bindForm", formObj, slotParms.callback_map));
                    }
                    
                    if (slotParms.location_disp.length > 0) {
                        $("#breadcrumb").html("<a href='Member_announce'>Home</a> / " + slotParms.location_disp + " Signup");
                    }
                    
                    if(ftCount(slotParms.callback_form_map) > 0 || slotParms.callback_form_list.length){
                        var cbfObj = slotParms.callback_form_map;
                        if(slotParms.callback_form_list.length){
                            cbfObj = slotParms.callback_form_list;
                        }
                        var formContainer = $('<div class="main_instructions field_list_container"></div>');
                        modalObj.append(formContainer);
                        formContainer.append(obj.foreTeesModal("buildForm", cbfObj, slotParms.callback_map, slotParms));
                    }
                    
                    // Bind to any form buttons
                    modalObj.find('button.continue_slot, input.continue_slot[type="button"]').click(function(){
                        slotParms.soc();
                    });
                    
                    var buttons = [];

                    if(slotParms.page_start_button_go_back){
                        buttons.push({
                            text:ftReplaceKeyInString(options.slotPageLoadNotification.goBackButton, slotParms),
                            click:function(){
                                $body.foreTeesModal("pleaseWait");
                                $(this).ftDialog("close");
                                obj.foreTeesSlot("leaveForm", false, true);
                            }
                        });
                    }
                    
                    if(slotParms.page_start_button_continue || slotParms.page_start_button_accept){
                        var button_text = options.slotPageLoadNotification.continueButton;
                        if(slotParms.page_start_button_accept){
                            button_text = options.slotPageLoadNotification.acceptButton;
                        }
                        buttons.push({
                            text:ftReplaceKeyInString(button_text, slotParms),
                            click:function(){
                                slotParms.soc();
                            }
                        });
                    }
                    
                    if(slotParms.callback_button_map){
                        for(var key in slotParms.callback_button_map){
                            var buttonMap = slotParms.callback_button_map[key];
                            var button_value = ftReplaceKeyInString(buttonMap.value, slotParms);
                            var buttonClass = "";
                            if(buttonMap["class"]){
                                buttonClass = buttonMap["class"];
                            }

                            // Pass key, buttonMap, button_value through a function that returns a function 
                            // to lock the values to what they are at this point in the loop.
                            buttons.push({
                                text:button_value,
                                'class':buttonClass,
                                click:function(key, buttonMap, button_value){
                                    return function(){
                                        if("modal" in buttonMap){
                                            obj.foreTeesModal(buttonMap.modal.method, buttonMap.modal.data);
                                            return;
                                        }
                                        if("suppress" in buttonMap){
                                            for(var i = 0; i < buttonMap.suppress.length; i++){
                                                delete slotParms.callback_map[buttonMap.suppress[i]];
                                            }
                                        }
                                        if("set" in buttonMap){
                                            for(var skey in buttonMap.set){
                                                slotParms.callback_map[skey] = buttonMap.set[skey];
                                            }
                                        }
                                        
  
                                        var confirmed = false;
                                        if(buttonMap.confirm){
                                            confirmed = confirm(buttonMap.confirm);
                                        } else {
                                            confirmed = true;
                                        }
                                        if("require" in buttonMap){
                                            for(var rkey in buttonMap.require){
                                                if(!(rkey in slotParms.callback_map) || slotParms.callback_map[rkey] == ""){
                                                    confirmed = false;
                                                    alert(buttonMap.require[rkey]);
                                                    break;
                                                }
                                            }
                                        }
                                        if("replace" in buttonMap){
                                            // Redirect to specified URL, but do not affect browser history
                                            var target = window.location.href;
                                            for(var skey in buttonMap.replace){
                                                if(skey == "target"){
                                                    target = buttonMap.replace[skey];
                                                } else if(skey == "suppress_unload") {
                                                    slotParms.skip_unload = true;
                                                } else {
                                                    slotParms.callback_map[skey] = buttonMap.replace[skey];
                                                }
                                            }
                                            $body.foreTeesModal("pleaseWait");
                                            window.location.replace(target+"?"+ftObjectToUri(slotParms.callback_map,{"json_mode":true,"use_json":true,"json":true}));
                                            confirmed = false;
                                        }
                                        if(confirmed){
                                            if(typeof buttonMap.action != "undefined"){
                                                obj.foreTeesSlot(buttonMap.action);
                                            }else{
                                                slotParms.callback_map[key] = button_value;
                                                slotParms.soc();
                                            }
                                        }
                                            
                                    }
                                }(key, buttonMap, button_value)
                            });
                        }
                    }
                   
                    modalObj.ftDialog("option",{
                        beforeClose: function(){
                            $body.removeClass('ftSPLOpen');
                        },
                        buttons: buttons

                    });
                    ftInitPageHelp(modalObj);
                    ftActivateElements(modalObj);
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
                        
                        //var obj = $(this).data("ft-modalParent");
                        //var slotParms = obj.data("ft-slotParms");
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
 
        slotSubmit: function (option, mode, obj) {
            
            if(!obj){
                obj = $(this);
            }
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
                case "cancel_prompt":
                    slotParms.last_submit_mode = mode;
                    formValues["remove"] = "submit";
                    formValues["slot_submit_action"] = "delete";
                    break;
                case "cancel_slot":
                    slotParms.last_submit_mode = mode;
                    formValues["remove"] = "submit";
                    formValues["ack_remove"] = "true";
                    formValues["slot_submit_action"] = "delete";
                    break;
                case "submit_slot":
                    slotParms.last_submit_mode = mode;
                    formValues["submitForm"] = "submit";
                    formValues["slot_submit_action"] = "update";
                    break;    
            }
            formValues["json_mode"] = true;
            
            //console.log("submitting form");
            //console.log(JSON.stringify(formValues));
            
            var i;

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
                        //context: obj,
                        success: function(data, textStatus, jqXHR){
                            //var obj = $(this);
                            //var slotParms = obj.data("ft-slotParms");
                            //var modalObj = obj.data("ft-slotSubmitObj");
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
                            
                                // First check if this is session timeout response
                                if(data.indexOf('Access Error - Please Read')>-1){
                                    // Send this as an ajax error
                                    slotParms.skip_unload = true;
                                    methods.ajaxError({
                                        jqXHR:{
                                            responseText:data
                                        }
                                    }, obj);
                                return;
                            }
                                
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
                            message_body = message_body.replace(/<link[^>]*>/ig, "");
                            message_body = message_body.replace(/<\!doctype[^>]*>/ig, "");
                            message_body = message_body.replace(/<body[^>]*>/ig, "");
                            message_body = message_body.replace(/<\/body[^>]*>/ig, "");
                            message_body = message_body.replace(/<html[^>]*>/ig, "");
                            message_body = message_body.replace(/<\/html[^>]*>/ig, "");
                                
                            message_body = message_body.replace(/<script[^>]*>((?:[\s\S](?!<\/script))*)[^<]*<\/script[^>]*>/ig, "");
                                
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
                       
                            message_body = message_body.replace(/<head[^>]*>((?:[\s\S](?!<\/head))*)[^<]*<\/head[^>]*>([\r\n\s]*[^<a-z0-9]*[\r\n\s]*[^<a-z0-9]*[\r\n\s]*<br>[\r\n\s]*)*/ig, "");
                            //message_body = message_body.replace(/<title[^>]*>((?:[\s\S](?!<\/title))*)[^<]*<\/title[^>]*>([\r\n\s]*[^<a-z0-9]*[\r\n\s]*[^<a-z0-9]*[\r\n\s]*<br>[\r\n\s]*)+/ig, "");
                            //message_body = message_body.replace(/<title[^>]*>((?:[\s\S](?!<\/title))*)[^<]*<\/title[^>]*>/ig, "");
                            
                            // console.log(message_body);
                            result.message_list = [message_body];
                            
                            // Check if we will be returning to the slot page
                            result.back_to_slotpage = (data.match(/(<input type=["']hidden["'] name=["']player1["'] value=|(action|href)=["']javascript:history.back\()/i) != null);
                            // Check if we were successful
                            result.successful = (data.match(/Your [a-z\s0-9]+ has been (accepted|processed) and (accepted|processed)/i) != null);
                            if(!result.successful){
                                result.successful = (data.match(/(The reservation has been cancelled|the request has been removed from the system|The event entry has been cancelled|The wait list entry has been cancelled|The notification has been cancelled)/i) != null);
                            }

                        }

                        if(!result.successful && result.page_start_button_go_back){
                            // This is a page start or error noticication -- 
                            // we'll partially convert it to a slot page response
                            result.back_to_slotpage = false;
                            result.title = result.page_start_title;
                            //result.head = result.page_start_title;
                            result.notice_list = result.page_start_notifications;
                            if(result.page_start_instructions){
                                result.message_list =  result.page_start_instructions;
                            }
                            result.successful = true;
                        }
                            
                        if(!result.back_to_slotpage && !result.successful && !result.prompt_yes_no && !result.prompt_close_continue){
                            ft_logError("Error Submitting Slot and user will be unable to resolve", jqXHR); 
                        }
                            
                        slotParms.this_callback = result;
                            
                        /*
                            console.log(result.callback_map);
                            if(result.callback_map){
                                result.callback_map = ftMixedObjectToObject(result.callback_map);
                            }
                            console.log(result.callback_map);
                            */
                           
                        //console.log(slotParms);
                            
                        var message = '';
                        if("warning_list" in result && (result.warning_list.length || result.warning_head)){
                            message += '<div class="sub_instructions">';
                            if(result.warning_head){
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
                            
                        if(result.message_array && !result.message_list){
                            result.message_list = result.message_array;
                        }
                            
                        if(result.message_list && result.message_list.length){
                            var block_head = '<div class="main_instructions">';
                            if(!result.successful){
                                block_head = '<div class="sub_instructions">';
                            }else{
                                slotParms.skip_unload = true;
                            }
                            message += block_head;
                            var c = 0;
                            for(i in result.message_list){
                                var text = result.message_list[i];
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
                        }
                            
                        if("notice_list" in result && (result.notice_list.length || result.notice_head)){
                            message += '<div class="sub_instructions">';
                            if(result.notice_head){
                                message += '<h3>'+ftReplaceKeyInString(result.notice_head, slotParms)+'</h3>';
                            }
                            for(i in result.notice_list){
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
                            
                        if(ftCount(result.callback_form_map) > 0 || (result.callback_form_list && result.callback_form_list.length)){
                            var cbfObj = result.callback_form_map;
                            if(result.callback_form_list && result.callback_form_list.length){
                                cbfObj = result.callback_form_list;
                            }
                            var formContainer = $('<div class="main_instructions field_list_container"></div>');
                            modalObj.append(formContainer);
                            formContainer.append(methods.buildForm(cbfObj, result.callback_map, slotParms, result.required_field_color));
                        }
                            
                        if("form_html" in result){
                            //console.log("binding form");
                            var formObj = $('<form>'+ftReplaceKeyInString(result.form_html, slotParms)+'</form>');
                            modalObj.append(obj.foreTeesModal("bindForm", formObj, result.callback_map));
                        }
                            
                        ftActivateElements(modalObj);
                            
                        var buttons = [];
                            
                        var continueButtonText = options.slotSubmit.continueButton;
                        var closeButtonText = options.slotSubmit.closeButton;
                        if(result.continue_button){
                            continueButtonText = result.continue_button;
                        }
                        if(result.close_button){
                            closeButtonText = result.close_button;
                        }
                            
                        // Build our close/continue button
                        if(!result.prompt_yes_no && !result.prompt_close_continue){
                            if(!result.back_to_slotpage){
                                obj.foreTeesSlot("leaveForm");
                                buttons.push({
                                    text:ftReplaceKeyInString(continueButtonText, slotParms),
                                    click:function(){
                                        //var obj = $(this).data("ft-modalParent");
                                        //var slotParms = obj.data("ft-slotParms");
                                        slotParms.skip_unload = true;
                                        obj.foreTeesSlot("exitSlotPage");
                                    }
                                });
                            }else{
                                modalObj.foreTeesModal("showTitleClose");
                                buttons.push({
                                    text:ftReplaceKeyInString(closeButtonText, slotParms),
                                    click:function(){
                                        $(this).ftDialog("close");
                                    }
                                });
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
                                backButton = ftReplaceKeyInString(closeButtonText, slotParms);
                                continueButton = ftReplaceKeyInString(continueButtonText, slotParms); 
                            }
                            buttons.push({
                                text:backButton,
                                click:function(){
                                    modalObj.ftDialog("close");
                                }
                            });
                            buttons.push({
                                text:continueButton,
                                click:function(){
                                    //var obj = $(this).data("ft-modalParent");
                                    //var slotParms = obj.data("ft-slotParms");
                                    var confirmed = true;
                                    if("require" in result){
                                        for(var rkey in result.require){
                                            if(!(rkey in result.callback_map) || result.callback_map[rkey] == ""){
                                                confirmed = false;
                                                alert(result.require[rkey]);
                                                break;
                                            }
                                        }
                                    }
                                    if(confirmed){
                                        slotParms.alternate_parms = result.callback_map;
                                        slotParms.skip_unload = true;
                                        modalObj.ftDialog("close");
                                        obj.foreTeesSlot("submitForm");
                                        slotParms.skip_unload = false;
                                    }
                                }
                            });
                        }
                        modalObj.ftDialog("option",{

                            buttons: buttons,
                            title: ftReplaceKeyInString(result.title, slotParms)

                        });
                            
                        // Reposition and resize the window
                        modalObj.foreTeesModal("triggerResize");
                            
                    },
                    error: function(xhr, e_text, e) {
                        obj.foreTeesModal("pleaseWait", "close");
                        methods.ajaxError({
                            title:"Error Submitting Reservation",
                            jqXHR: xhr, 
                            textStatus: e_text, 
                            errorThrown: e,
                            allowBack: true,
                            tryAgain: function() {
                                methods.slotSubmit(option, mode, obj);
                            }
                        },obj);
                    }
                    });
                    
                break;
            case "close":
                modalObj.ftDialog("close");
                break;
        }
          
    },
        
        
    linkModal: function (new_option, obj) {
            
        if(!obj){
            obj = $(this);
        }
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
                    error: function(xhr, e_text, e) {
                        obj.foreTeesModal("pleaseWait", "close");
                        methods.ajaxError({
                            title:"Error Loading Modal",
                            jqXHR: xhr, 
                            textStatus: e_text, 
                            errorThrown: e, 
                            allowClose: true,
                            allowBack: true,
                            tryAgain: function() {
                                methods.linkModal(new_option, obj);
                            }
                        },obj);
                    }
                });
                    
                break;
            case "close":
                modalObj.dialog("close");
                break;
        }
          
    },


    guestDbSubmit: function (option, parms, obj) {
            
        if(!obj){
            obj = $(this);
        }
  
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
                    error: function(xhr, e_text, e) {
                        obj.foreTeesModal("pleaseWait", "close");
                        methods.ajaxError({
                            title:"Error submitting Guest Database Entry",
                            jqXHR: xhr, 
                            textStatus: e_text, 
                            errorThrown: e, 
                            allowClose: true,
                            allowBack: true,
                            tryAgain: function() {
                                methods.guestDbSubmit(option, parms, obj);
                            }
                        },obj);
                    }
                });
                    
                break;
            case "close":
                modalObj.ftDialog("close");
                break;
        }
          
    },

    guestDbPrompt: function (option, parms, obj) {
            
        if(!obj){
            obj = $(this);
        }
        var i;
        var rwd = $.fn.foreTeesSession('get','rwd');

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
                        var message = '<div class="main_instructions pageHelp" data-fthelptitle="Instructions">';
                        for(i = 0; i < data.message_list.length; i++){
                            message += '<p>'+data.message_list[i]+'</p>';
                        }
                        message += '</div>';

                        message +=  '<div class="forms_container">';
                        if(rwd){
                            message += '<ul class="ftGdb-tabs"><li><div data-ftTab=".ftGdb-guestSelect">Search Guests</div></li><li><div data-ftTab=".ftGdb-guestAdd">Add Guests</div></li></ul>';
                        }
                        message += '<div class="right_container ftGdb-guestSelect ftGdb-block">';
                        message += '<div class="sub_instructions modal_guest_list">';
                        if(!rwd){
                            message += '<select name="guestselect" size="15"></select>';
                        }
                        message += '</div>';
                        message += '<div style="clear:both;"></div>';
                        message += '</div>'; // end right container
                            
                        message += '<div class="left_container ftGdb-guestAdd  ftGdb-block">';
                            
                        if(data.guest_info_fields.field_notes.length > 0){
                            message += '<div class="sub_instructions">';
                            for(i = 0; i < data.guest_info_fields.field_notes.length; i++){
                                message += '<p>'+data.guest_info_fields.field_notes[i]+'</p>';
                            }
                            message += '</div>';
                        }
                            
                        message += '<div class="main_instructions field_list_container">';

                        message += '<div style="clear:both;"></div></div>'; // end sub intructions
                        message += '<div style="clear:both;"></div>';
                        message += '</div>'; // end left container
                            
                            
                            
                        message += '</div>';// end forms container
                            
                        modalObj.append(message);
                            
                            
                            
                        var fieldListObj = modalObj.find(".field_list_container");
                        var rightContainer = modalObj.find(".right_container");
                        var leftContainer = modalObj.find(".left_container");
                            
                        fieldListObj.append(obj.foreTeesModal("buildForm", data.guest_info_fields.fields));
                            
                        if(rwd){
                            // Responsive mode
                            var guestList = rightContainer.find('.modal_guest_list');
                            //console.log(data.guest_db_list);
                            guestList.foreTeesMemberSelect("init",{
                                guestDb: true,
                                guestDbList: data.guest_db_list,
                                onGuestDbSelect:function(record, o){
                                    parms.callback(record);
                                }
                            });
                        } else {
                            // Desktop mode
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
                        }
                            

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
                        ftActivateElements(modalObj);
                        ftInitPageHelp(modalObj);
                        var ac = "active";
                        var tabs = modalObj.find('.ftGdb-tabs');
                        var blocks = modalObj.find('.ftGdb-block');
                        tabs.find('li>div').click(function(e){
                            var el = $(this);
                            tabs.find('li>div').removeClass(ac);
                            el.addClass(ac);
                            blocks.removeClass(ac);
                            modalObj.find(el.attr('data-ftTab')).addClass(ac);
                            e.preventDefault();
                        });
                        tabs.find('li>div').first().click();
                        // Set the size of the right and left column to be the same
                            
                        if(rwd){
                            modalObj.data("ft-postResize",function(){
                                rightContainer.height(leftContainer.height()); 
                            });
                        } else {
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
                        }
                        // Reposition and resize the window
                            
                        modalObj.foreTeesModal("triggerResize");
                            
                            
                    },
                    error: function(xhr, e_text, e) {
                        obj.foreTeesModal("pleaseWait", "close");
                        methods.ajaxError({
                            title:"Error getting Guest Database",
                            jqXHR: xhr, 
                            textStatus: e_text, 
                            errorThrown: e, 
                            allowClose: true,
                            allowBack: true,
                            tryAgain: function() {
                                methods.guestDbPrompt(option, parms, obj);
                            }
                        },obj);
                    }
                });
                    
                break;
            case "close":
                modalObj.ftDialog("close");
                break;
        }
          
    },

    eventPrompt: function (new_option, obj) {

        if(!obj){
            obj = $(this);
        }

        var option = {};

        if(typeof new_option == "object"){
            option = $.extend({}, options.eventPrompt, new_option);
        } else {
            option = $.extend({}, options.eventPrompt);
            option.mode = new_option;
        }


        // Build url for JSON request via REST.
        var url = option.servlet+"?jsonMode=true&" + ftObjectToUri(option, {
            servlet:true
        });
        var modalObj = obj.foreTeesModal("getModalObject","eventPrompt");

        switch (option.mode) {
            default:
            case "open":
                methods.pleaseWait("open",null,null,obj);
                $.ajax({
                    url: url,
                    //context:obj,
                    dataType: 'json',
                    success: function(data){
                        methods.pleaseWait("close",null,null,obj);
                        option.data = data;
                        var key;
                        if(typeof data.access_error == "string" && data.access_error.length){
                            obj.foreTeesModal("eventPrompt", "error", data.access_error);
                            return;
                        }

                        modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(data.prompt_title, option));
                        modalObj.empty();
                        modalObj.ftDialog("open");
                        modalObj.foreTeesModal("hideActivity");

                        var message = "";

                        if( (
                            (data.instructions && data.instructions.length) 
                            || 
                            (data.prompt_instructions && data.prompt_instructions.length))){
                            // Display instructions
                            message += '<div class="sub_instructions pageHelp" data-fthelptitle="Instructions">';
                            for (key in data.instructions){
                                message += '<p class="instruction_block">'+ftReplaceKeyInString(data.instructions[key], option)+'</p>';
                            }
                            for (key in data.prompt_instructions){
                                message += '<p class="instruction_block">'+ftReplaceKeyInString(data.prompt_instructions[key], option)+'</p>';
                            }
                            message += '</div>';
                        }

                        message += '<div class="main_instructions">';
                        message +='<ul class="clearfix modal_field_list">';
                        // Display fields
                        for(var i in data.detail_list){
                            var entry = data.detail_list[i];
                            if(typeof entry["class"] == "string"){
                                message +='<li class="clearfix '+entry["class"]+'">';
                            }else{
                                message +='<li class="clearfix">';
                            }
                            message += '<div class="clearfix"><b>'+entry.label+':</b><div>'+entry.value+'</div></div>';
                            message +='</li>';
                        }
                        message += '</ul>';
                        message += '</div>';

                        var buttons = [];
                        buttons.push({
                            text: option.closeButton,
                            click: function(){
                                $(this).ftDialog("close");
                            }
                        });
                        if(data.use_signup_list && data.signup_list && data.signup_list.length){
                            // Create view/edit list button
                            buttons.push({
                                text:[option.data.list_button],
                                click: function(){
                                    methods.eventList({
                                        data:data
                                    }, obj);
                                }
                            });
                        }
                        var instructions = "";
                        if(data.signup){

                            if(typeof data.status == "string"){
                                message += '<div class="sub_instructions">';
                                message += '<p class="instruction_block">'+ftReplaceKeyInString(data.status, option)+'</p>';
                                message += '</div>'
                            }
                            // Check if user is registered
                            if(data.in_event){
                                message += '<div class="main_instructions">';
                                message += option.registered;
                                message += '</div>'
                            } else {
                                message += '<div class="sub_instructions">';
                                message += option.unregistered;
                                message += '</div>'
                            }

                            // Crate sign-up button
                            if(data.use_signup_button){
                                buttons.push({
                                    text:option.data.signup_button,
                                    click:function(){
                                        var buttonData = {};
                                        buttonData[data.event_id_field] = data.event_id;
                                        buttonData.base_url = data.base_url;
                                        buttonData['new'] = true;
                                        ftSlotPost(data.slot_url,buttonData);
                                    }
                                });
                            }
                            if(data.my_signup_id){
                                buttons.push({
                                    text:option.data.edit_signup_button,
                                    click:function(){
                                        var buttonData = {};
                                        buttonData[data.event_id_field] = data.event_id;
                                        buttonData.base_url = data.base_url;
                                        buttonData[data.signup_id_field] = data.my_signup_id;
                                        buttonData.edit = true;
                                        ftSlotPost(data.slot_url,buttonData);
                                    }
                                });
                            }

                        }else{
                            // Display reason for no signup buttons
                            if(typeof data.block_reason == "string"){
                                instructions += '<div class="sub_instructions">';
                                instructions += '<p class="instruction_block"><b>'+ftReplaceKeyInString(data.block_reason, option)+'</b></p>';
                                instructions += '</div>'
                            }
                        }

                        modalObj.append(message);
                        modalObj.append(instructions);
                        ftActivateElements(modalObj);
                        ftInitPageHelp(modalObj);
                        modalObj.ftDialog("option",{

                            buttons: buttons,
                            beforeClose: function(ev,ui){
                                if($("#fakeClick.event_button").length){
                                    // We got here from an external link
                                    methods.pleaseWait("open");
                                    ft_historyBack();
                                    return false;
                                } else {
                                    return true;
                                }
                            }

                        });

                        modalObj.foreTeesModal("triggerResize");

                    },
                    error: function(xhr, e_text, e) {
                        methods.pleaseWait("close",null,null,obj);
                        methods.ajaxError({
                            title:"Error Loading Event",
                            jqXHR: xhr, 
                            textStatus: e_text, 
                            errorThrown: e, 
                            allowClose: true,
                            allowBack: true,
                            tryAgain: function() {
                                methods.eventPrompt(new_option, obj);
                            }
                        },obj);
                    }

                });

                break;
            case "close":
                modalObj.ftDialog("close");
                break;
        }

    },

    eventList: function (new_option, obj) {
            
        if(!obj){
            obj = $(this);
        }
            
        var option = {};
            
        if(typeof new_option == "object"){
            option = $.extend({}, options.eventList, new_option);
        } else {
            option = $.extend({}, options.eventList);
            option.mode = new_option;
        }
        var rwd = $.fn.foreTeesSession('get','rwd');
        var user = $.fn.foreTeesSession('get','user');

        var modalObj = obj.foreTeesModal("getModalObject","eventList");
   
        var t = local.getTagNames();
            
        switch (option.mode) {
            default:
            case "open":
                    
                // Check object
                var data = option.data;
                if((typeof data != "object")){
                    // No data was sent.  Try to load it via ajax
                    var url = option.servlet+"?jsonMode=true&" + ftObjectToUri(option,{
                        servlet:true
                    });
                    methods.pleaseWait("open",null,null,obj);
                    $.ajax({
                        url: url,
                        //context:obj,
                        dataType: 'json',
                        success: function(data){
                            methods.pleaseWait("close",null,null,obj);
                            new_option.data = data;
                            // Got the data, so display it.
                            methods.eventList(new_option, obj);
                        },
                        error: function(xhr, e_text, e) {
                            methods.pleaseWait("close",null,null,obj);
                            methods.ajaxError({
                                title:"Error Loading Event Listing",
                                jqXHR: xhr, 
                                textStatus: e_text, 
                                errorThrown: e, 
                                allowClose: true,
                                allowBack: true,
                                tryAgain: function() {
                                    methods.eventList(new_option, obj);
                                }
                            },obj);
                        }
                    });
                    return;
                }
                if(option.date){
                    modalObj.foreTeesModal("initModalObject","");
                    var tb = modalObj.closest(".ui-dialog").find(".ui-dialog-title");
                    tb.append(data.list_title);
                    
                    
                } else {
                    modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(data.list_title, option));
                }
                
                
                    
                // build table(s)
                var table2;
                var table = t.fn.oTable('standard_list_table rwdCompactible');
                // build header
                table += t.fn.oThead()+t.fn.oTr();
                table += t.fn.th(data.select_column);
                if(data.use_location){
                    table += t.fn.th("Location");
                }
                if(data.use_type){
                    table += t.fn.th("Type");
                }
                //if(!data.status_per_player){
                table += t.fn.th('Status');
                //}
                table += t.fn.th(data.players_column);
                table += t.fn.cTr()+t.fn.cThead();
                    
                // Build rows
                table += t.fn.oTbody();
                var by_name = [], player, i, temp, ld;
                    
                for (i = 0; i < data.signup_list.length; i++){
                    var signup = data.signup_list[i];
                    table += t.fn.oTr();
                    if(signup.id && data.signup){
                        temp = t.fn.tag('a',signup.select_button,'standard_button event_select_button',{
                            href:'#',
                            'data-ftjson':JSON.stringify({
                                id:signup.id
                            })
                        });
                    } else {
                        temp = t.fn.tag('div',signup.select_button,'event_slot');
                    }
                    table += t.fn.td(temp,'sT');
                    if(data.use_location){
                        table += t.fn.td(signup.location,'sN');
                    }
                    if(data.use_type){
                        if(signup.event_id && data.signup){
                            ld = {type:signup.slot_type};
                            ld[data.event_id_field] = signup.event_id;
                            temp = t.fn.tag('a',signup.type,'event_button standard_link',{
                                href:'#',
                                'data-ftjson':JSON.stringify(ld)
                            });
                        } else {
                            temp = signup.type;
                        }
                        table += t.fn.td(temp,'sP sTp');
                    }
                    //if(!data.status_per_player){
                    table += t.fn.td(signup.status,data.use_location?'sP sSt':'sN');
                    //}
                    temp = '';
                    switch (data.players_column){
                        case "Reservees":
                            temp = " sR";
                            break;
                    }
                    table += t.fn.oTd('sP'+temp);
                    for (var i2 = 0; i2 < signup.players.length; i2++){
                        player = signup.players[i2];
                        if(player.user && player.name != "X" && !data.hide_names){
                            player.signup = signup;
                            by_name.push(player); // Only add members to the by name list
                        }
                        temp = '';
                        if(player.cw){
                            temp = t.fn.tag('span',player.cw);
                        } else if(player.status && signup.show_player_status){
                            data.status_per_player_afer = true; // Force it for the name list
                            temp = t.fn.tag('span',player.status);
                        }
                        table += t.fn.tag('div',t.fn.tag('span',player.name)+temp,player.user==user?'me':'');
                    }
                    table += t.fn.cTd();
                    table += t.fn.cTr();
                }
                table += t.fn.cTbody();
                table += t.fn.cTable();
                    
                // Alpha sort by name list
                by_name.sort(function(a,b){
                    return a.alpha_name < b.alpha_name ? -1 : 1;
                });
                    
                if(by_name.length){
                    // Build by name table
                    table2 = t.fn.oTable('standard_list_table rwdCompactible');
                    // build header
                    table2 += t.fn.oThead()+t.fn.oTr();
                    table2 += t.fn.th(data.select_column);
                    if(data.use_location){
                        table2 += t.fn.th("Location");
                    }
                    if(data.use_type){
                        table2 += t.fn.th("Type");
                    }
                    table2 += t.fn.th('Status');
                    table2 += t.fn.th(data.player_column);
                    table2 += t.fn.cTr()+t.fn.cThead();

                    // Build rows
                    table2 += t.fn.oTbody();
                        
                    for (i = 0; i < by_name.length; i++){
                        player = by_name[i];
                        signup = player.signup;
                        table2 += t.fn.oTr();
                        if(signup.id && data.signup){
                            temp = t.fn.tag('a',signup.select_button,'standard_button event_select_button',{
                                href:'#',
                                'data-ftjson':JSON.stringify({
                                    id:signup.id
                                })
                            });
                        } else {
                            temp = t.fn.tag('div',signup.select_button,'event_slot standard_link');
                        }
                        table2 += t.fn.td(temp,'sT');
                            
                        if(data.use_location){
                            table2 += t.fn.td(signup.location,'sN');
                        }
                        if(data.use_type){
                            if(signup.event_id && data.signup){
                                ld = {type:signup.slot_type};
                                ld[data.event_id_field] = signup.event_id;
                                temp = t.fn.tag('a',signup.type,'event_button',{
                                    href:'#',
                                    'data-ftjson':JSON.stringify(ld)
                                });
                            } else {
                                temp = signup.type;
                            }
                            table2 += t.fn.td(temp,'sP sTp');
                        }
                        table2 += t.fn.td((data.status_per_player||data.status_per_player_after)?player.status:signup.status,data.use_location?'sP sSt':'sN');
                        temp = '';
                        switch (data.player_column){
                            case "Reservee":
                                temp = " sRs";
                                break;
                            default:
                                temp = " sPs";
                                break;
                        }
                        table2 += t.fn.oTd('sP'+temp);
                        temp = '';
                        if(player.cw){
                            temp = t.fn.tag('span',player.cw);
                        }
                        table2 += t.fn.tag('div',t.fn.tag('span',player.alpha_name)+temp,player.user==user?'me':'');
                        table2 += t.fn.cTd();
                        table2 += t.fn.cTr();
                    }
                    table2 += t.fn.cTbody();
                    table2 += t.fn.cTable();
                }
                    
                //var buttons = obj.data("ft-eventListButtons");
                // add html to DOM

                var message = "", key;
                if( (
                    (data.instructions && data.instructions.length) 
                    || 
                    (data.list_instructions && data.list_instructions.length))){
                    // Display instructions
                    message += t.fn.oSubInst('pageHelp', {
                        'data-fthelptitle':'Instructions'
                    });
                    for (key in data.instructions){

                        message += t.fn.tag('p',ftReplaceKeyInString(data.instructions[key], option),'instruction_block');
                    //listInstructions += '<p class="instruction_block">'+ftReplaceKeyInString(data.instructions[key], listTextValues)+'</p>';
                    }
                    for (key in data.list_instructions){
                        message += t.fn.tag('p',ftReplaceKeyInString(data.list_instructions[key], option),'instruction_block');
                    //listInstructions += '<p class="instruction_block">'+ftReplaceKeyInString(data.instructions[key], listTextValues)+'</p>';
                    }
                    message += t.fn.cSubInst();
                }
                       
                       
                    
                modalObj.append(message);
                if(!table2){
                    modalObj.append(table);
                } else {
                    modalObj.append(t.fn.tabs('ftEventNameList',{
                        ftEventListReservations:'By Reservation',
                        ftEventListNames:'By Member'
                    }));
                    modalObj.append(t.fn.tabBlocks('ftEventNameList',{
                        ftEventListReservations:table,
                        ftEventListNames:table2
                    }));
                }
                    
                if(data.list_status){
                    modalObj.append(t.fn.subInst(data.list_status));
                }
                    
                modalObj.find(".event_select_button").click(function(e){
                    e.preventDefault();
                    var buttonData = JSON.parse($(this).attr("data-ftjson"));
                    if(data.event_id){
                        buttonData[data.event_id_field] = data.event_id;
                    }
                    buttonData.base_url = data.base_url;
                    var id = buttonData.id;
                    delete buttonData.id;
                    buttonData[data.signup_id_field] = id;
                    buttonData.edit = true;
                    ftSlotPost(data.slot_url,buttonData);
                //return false;
                });
                    
                var buttons = [];
                buttons.push({
                    text: option.closeButton,
                    click: function(){
                        $(this).ftDialog("close");
                    }
                });
                if(data.use_signup_button && data.signup){
                    buttons.push({
                        text:option.data.signup_button,
                        click:function(){
                            var buttonData = {};
                            buttonData[data.event_id_field] = data.event_id;
                            buttonData.base_url = data.base_url;
                            buttonData['new'] = true;
                            ftSlotPost(data.slot_url,buttonData);
                        }
                    });
                } else {
                //??
                }
                modalObj.ftDialog("option",{

                    buttons: buttons,
                    positon:"top"

                });

                modalObj.ftDialog("open");
                    
                modalObj.closest(".ui-dialog").find(".reservation_date_picker").change(function(){
                    new_option.date = ftMDYToInt($(this).val());
                    delete new_option.data;
                    methods.eventList(new_option, obj);
                });
                    
                ftActivateElements(modalObj.closest(".ui-dialog"));
                ftInitPageHelp(modalObj);
                    
                modalObj.foreTeesModal("resize");
                modalObj.foreTeesModal("hideActivity");
                break;
                    
            case "close":
                modalObj.ftDialog("close");
                break;
        }
          
    },
        
        // **** NEEDS CLEAN-UP
    lotteryPrompt: function (option, option2, obj) {
            
        if(!obj){
            obj = $(this);
        }
        // Check Json
        var jsondata = obj.attr("data-ftjson");
        try {
            var json = JSON.parse(jsondata);
        //// console.log('Parsed Json');
        } catch (e) {
            //// console.log('Bad Json');
            return;
        }
        var base_url = "";
        if(json.base_url){
            base_url = json.base_url;
        }
        if(json.type == "Member_lott"){
            // Don't prompt -- go directly to lottery page
            obj.foreTeesModal("pleaseWait","open",true);
            var formObj = $('<form action="'+base_url+ftSetJsid('Member_lott')+'" method="post">' + ftObjectToForm(json, {}) + '</form>'); 
            $('body').append(formObj);
            formObj.submit();
            return;
        }
        // Build url for JSON request via REST.
        json.stime = ftFindKey("time",json);
        obj.data("ft-lotteryRequest", json);
        var url = base_url+"Member_mlottery?jsonMode=true&" + ftObjectToUri(json, {});
        
        
            
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
                            return;
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
                            viewEditButtonName = options.lotteryPrompt.viewEditButton;
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
                            var formHtml = '<form action="'+base_url+ftSetJsid('Member_lott')+'" method="post">' + ftObjectToForm(request,{}) + '</form>';
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
                    error: function(xhr, e_text, e) {
                        obj.foreTeesModal("pleaseWait", "close");
                        methods.ajaxError({
                            title:"Error Loading Lottery Details",
                            jqXHR: xhr, 
                            textStatus: e_text, 
                            errorThrown: e, 
                            allowClose: true,
                            allowBack: true,
                            tryAgain: function() {
                                methods.lotteryPrompt(option, option2, obj);
                            }
                        },obj);
                    }
                    
                });
                    
                break;
            case "close":
                modalObj.ftDialog("close");
                break;
        }
          
    },
        // *** NEED CLEAN-UP
    lotteryList: function (option) {
            
        var obj = $(this);
            
        var rwd = $.fn.foreTeesSession('get','rwd');
        var user = $.fn.foreTeesSession('get','user');

        // Check object
        var data = obj.data("ft-lotteryPromptData");
        //// console.log(promptData.wait_list_id );
        if((typeof data != "object")){
            //// console.log('No lottery prompt object');
            return;
        }
        var request = obj.data("ft-lotteryRequest");
        if(typeof request != "object"){
            //// console.log('No lotteryPrompt JSON');
            return;
        }

        var itemDate = ftIntDateToDate(request.date);
        // Set text values used by text options ( use "["key-name"]" in option)
            
        var textValues = obj.data("ft-lotteryTextValues");
            
        var modalObj = obj.foreTeesModal("getModalObject","lotteryList");
        //modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(options.lotteryList.title, textValues));
            
        var t = local.getTagNames();
            
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
                message += '<'+t.table+' class="rwdTable standard_list_table rwdCompactible">';
                message += '<'+t.thead+' class="rwdThead"><'+t.tr+' class="rwdTr">';
                message += '<'+t.th+' class="rwdTh">Time</'+t.th+'>';
                if(use_course){
                    message += '<'+t.th+' class="rwdTh">Course</'+t.th+'>';
                }
                message += '<'+t.th+' class="rwdTh">F/B</'+t.th+'>';
                if(rwd){
                    message += '<'+t.th+' class="rwdTh">Players</'+t.th+'>';
                } else {
                    message += '<'+t.th+' class="rwdTh">Player 1</'+t.th+'><'+t.th+' class="rwdTh">Player 2</'+t.th+'><'+t.th+' class="rwdTh">Player 3</'+t.th+'><'+t.th+' class="rwdTh">Player 4</'+t.th+'>';
                    if(data.fives != 0){
                        message += '<'+t.th+' class="rwdTh row_player_5">Player 5</'+t.th+'>';
                    }
                }
                message += '</'+t.tr+'></'+t.thead+'>';
                message += '<'+t.tbody+' class="rwdTbody">';

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
                    message += '<'+t.tr+' class="rwdTr '+rowClass+'"><'+t.td+' class=\"rwdTd sT\">';
                    if(data.requests[requestKey].allow){
                        buttonData = {
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
                    message += '</'+t.td+'>';
                    if(use_course){
                        message += '<'+t.td+' class="rwdTd sN">'+ data.requests[requestKey].lcourse + '</'+t.td+'>';
                    }
                    // Loop over each group
                    var i;
                    if(rwd){
                        message += '<'+t.td+' class=\"rwdTd sF\">'+options.lotteryList.frontBackDecode[data.requests[requestKey].fb.toString()]+'</'+t.td+'>';
                        message += '<'+t.td+' class=\"rwdTd sP\">';
                        for(i = 0; i < data.requests[requestKey].player_count; i ++){
                            message += '<div'+(data.requests[requestKey].lusers['luser_'+i]==user?' class="me"':'')
                            +'><span>'+data.requests[requestKey].players["player_" + i]+'</span></div>';
                        }
                        message += '</'+t.td+'>';
                    } else {
                        for (i = 0; i < data.requests[requestKey].player_count; i += data.requests[requestKey].players_per_group) {
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
                    }
                    message += '</'+t.tr+'>';
                }
                message += '</'+t.tbody+'>';
                message += '</'+t.table+'>';

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
                    $(this).foreTeesModal("pleaseWait","open",true);
                    var buttonData = JSON.parse(unescape($(this).attr("data-ftjson")));
                    var base_url = "";
                    if(buttonData.base_url){
                        base_url = buttonData.base_url;
                    }
                    var formObj = $('<form action="'+base_url+ftSetJsid('Member_lott')+'" method="post">' + ftObjectToForm(buttonData, {}) + '</form>'); 
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
        
    pleaseWait: function (option, popstate, newtimeout, obj) {
            
        if(!obj){
            obj = $(this);
        }
            
        var timeout = 145000; // pleaseWait timeout set to 145 seconds
        if(newtimeout){
            timeout = newtimeout;
        }
        var modalObj = obj.foreTeesModal("getModalObject","pleaseWait");
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
 
                modalObj.foreTeesModal("initModalObject",'',{
                    noTitlebar:true
                });
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
                } else if (popstate && (ftIsMobile("ie10"))){
                    window.addEventListener('popstate pageshow',function(){
                        history.go(0);
                    },false);
                } else {
                    timers.push(setTimeout(function(){
                        obj.foreTeesModal("timeoutError");
                    },timeout));
                }
 
                var dialogObj = modalObj.parents('.ui-dialog').first();
                dialogObj.removeClass('ui-widget-content');
                modalObj.ftDialog("open");
        
                    
                break;
            case "close":
            
                if(modalObj.hasClass("ui-dialog-content")){
                    modalObj.ftDialog("close");
                }

                    
                clearTimeout(timers.pop());
                if(popstate && ftIsMobile("ios")){
                    $(window).unbind('popstate.ft-pleaseWait');
                }
                break;
        }
          
    },
        
    waitListPrompt: function (option, obj) {
            
        if(!obj){
            obj = $(this);
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
        //obj.data("ft-waitListRequest", json);
        // Build url for JSON request via REST.
        var url = "Member_waitlist?jsonMode=true&" + ftObjectToUri(json, {
            type:false
        });
            
        var modalObj = obj.foreTeesModal("getModalObject","waitListPrompt");
        //modalObj.foreTeesModal("initModalObject",ptions.waitListPrompt.title);
        obj.foreTeesModal("pleaseWait","open");
            
        switch (option) {
            default:
            case "open":
                
                modalObj.foreTeesModal("initModalObject",options.waitListPrompt.title);

                $.ajax({
                        
                    url: url,
                    //context:obj,
                    dataType: 'json',
                    success: function(data){
                        
                        modalObj.ftDialog("open");
                        obj.foreTeesModal("pleaseWait","close");
                        
                        //var obj = $(this);
                        //var modalObj = obj.data("ft-waitListPromptObj");
                        var waitlistDate = new Date (data.date);
                        modalObj.empty();
                        //obj.data("ft-waitListPromptData", data);
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
                                modalObj.ftDialog("close");
                            }
                        };
                        if(data.count > 0 && data.member_access > 0){
                            buttons["View Wait List"] = function(){
                                obj.foreTeesModal("waitList",{data:data});
                            }
                        }
                        if(typeof data.jump == "undefined"){
                            data.jump = "0";
                        }
                        var request = {
                            waitListId:data.wait_list_id,
                            sdate:waitlistDate.format("yyyymmdd"),
                            day:waitlistDate.format("dddd"),
                            index:data.index,
                            signupId:data.onlist,
                            course:json.course,
                            returnCourse:json.returnCourse,
                            jump:json.jump
                        }
                        if(data.onlist < 1 && data.member_access > 0){
                            buttons["Continue With Sign-up"] = function(){
                                ftSlotPost('Member_waitlist_slot',request);
                            }
                        }else if(data.member_access > 0){
                            buttons["Modify Your Sign-up"] = function(){
                                ftSlotPost('Member_waitlist_slot',request);
                            }
                        }
                            
                        modalObj.append(message);
                        modalObj.ftDialog("option",{

                            buttons: buttons

                        });
                        modalObj.foreTeesModal("triggerResize");

                    },
                    error: function(xhr, e_text, e) {
                        obj.foreTeesModal("pleaseWait", "close");
                        methods.ajaxError({
                            title:"Error Loading Wait List Details",
                            jqXHR: xhr, 
                            textStatus: e_text, 
                            errorThrown: e, 
                            allowClose: true,
                            allowBack: true,
                            tryAgain: function() {
                                methods.waitListPrompt(option, obj);
                            }
                        },obj);
                    }
                });
                    
                break;
            case "close":
                modalObj.ftDialog("close");
                break;
        }
          
    },
        
    waitList: function (new_option, obj) {
            
        if(!obj){
            obj = $(this);
        }
        
        var option = {};

        if(typeof new_option == "object"){
            option = $.extend({}, options.eventPrompt, new_option);
        } else {
            option = $.extend({}, options.eventPrompt);
            option.mode = new_option;
        }

        //var rwd = $.fn.foreTeesSession('get','rwd');
        var user = $.fn.foreTeesSession('get','user');

        // Check object
        var data = option.data;
        //// console.log(promptData.wait_list_id );
        if((typeof data == "object") && (typeof data.wait_list_id != "undefined")){
            var waitlistDate = new Date (data.date);
            var requestObj = {
                view:'current',
                waitListId:data.wait_list_id,
                sdate:waitlistDate.format("yyyymmdd"),
                index:data.index
            }
        }else{
            //// console.log('No waitlist prompt object');
            return;
        }

        // Build url for JSON request via REST.
            
        var url = "Member_waitlist?jsonMode=true&" + ftObjectToUri(requestObj,{});
        //// console.log(url);
            
        var modalObj = obj.foreTeesModal("getModalObject","waitList");
        //modalObj.foreTeesModal("initModalObject",ptions.waitList.title);
        
        obj.foreTeesModal("pleaseWait","open");
            
        var t = local.getTagNames();
            
        switch (option.mode) {
            default:
            case "open":
                modalObj.foreTeesModal("initModalObject",options.waitList.title);

                $.ajax({
                        
                    url: url,
                    //context:obj,
                    dataType: 'json',
                    success: function(response){
                        
                        modalObj.ftDialog("open");
                        obj.foreTeesModal("pleaseWait","close");
                            
                        //var obj = $(this);
                        //var modalObj = obj.data("ft-waitListObj");
                        //var data = obj.data("ft-waitListPromptData")
                        var waitlistDate = new Date (data.date);
                        modalObj.empty();
                        obj.data("ft-waitListData", response);
                        modalObj.parent().data("ft-activityIndicator").activity("stop");
                        modalObj.parent().data("ft-activityIndicator").hide();
                        modalObj.parent().find(".ui-dialog-titlebar-close").show();
                        var message = '';
                        message += t.fn.oMainInst();
                        message += t.fn.tag('h2',options.waitList.listName.replace("[list_name]",data.name));
                        message += t.fn.tag('p','<span>Date:</span><b>'+waitlistDate.format(options.waitList.dateFormat)+'</b>','waitlist_block');
                        message += t.fn.tag('p','<span>Course:</span><b>'+ data.course +'</b>','waitlist_block');
                        message += t.fn.tag('p','<span>Signups:</span><b>'+ ((data.member_view > 0)?data.count:'N/A') +'</b>','waitlist_block');
                        //message += '<p class="waitlist_block"><span>Wait List:</span><b>'+ data.start_time + ' to ' + data.end_time +'</b></p>';
                        message += t.fn.cMainInst();
                        message += t.fn.subInst(+options.waitList.listDate.replace("[report_date]",response.report_date));

                        message += t.fn.oTable('standard_list_table rwdCompactible');
                        message += t.fn.oThead()+t.fn.oTr();
                        message += t.fn.th('Pos');
                        message += t.fn.th('Desired Time');
                        message += t.fn.th('Players');
                        message += t.fn.cTr()+t.fn.cThead();

                        message += t.fn.oTbody();

                        var players = '';
                        var position = 1;
                        //console.log(response.signups);
                        // Loop over each signup
                        for (var signupKey in response.signups) {
                            var signup = response.signups[signupKey];
                            // Loop over each player
                            players = '';
                            for (var playerKey in response.signups[signupKey].players) {
                                var player = signup.players[playerKey];
                                players += t.fn.tag('div',t.fn.tag('span',player.name)+t.fn.tag('span',player.cw+(player["9hole"] > 0?'9':'')),(user.toLowerCase()==player.user.toLowerCase()?'me':''));
                            }
                            
                            message += t.fn.oTr();
                            
                            message += t.fn.td(''+position,'sPo');
                            message += t.fn.td(t.fn.tag('div',signup.start_time+' - '+signup.end_time,'time_slot'),'sT');
                            message += t.fn.td(players,'sP');
                            
                            message += t.fn.cTr();

                            position ++;
                        }
                        
                        message += t.fn.cTbody();
                        message += t.fn.cTable();
                            
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
                    error: function(xhr, e_text, e) {
                        obj.foreTeesModal("pleaseWait","close");
                        obj.foreTeesModal("ajaxError", {
                            title:"Error Loading Waitlist",
                            jqXHR: xhr, 
                            textStatus: e_text, 
                            errorThrown: e, 
                            allowBack: true,
                            allowClose: true,
                            allowRefresh: false,
                            tryAgain: function() {
                                methods.waitList(new_option, obj);
                            }
                        });
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

/*
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
*/
