/* 
 * Initialization for all devices
 */



/*************************************************************
 *
 * Initialize jQuery objects
 *  
 **************************************************************/
$.fn.foreTeesMemberCalendar("setOptions", {
    toolTips: {
        tlt:'Available',
        0:'View Only',
        1:'Tee Times Available',
        2:'Lottery'
    }
});

// jQuery start on DOM ready
$(document).ready(function() {
    
    if(ftapi){
        ftapi.updateClubDate($.fn.foreTeesSession("get","club_date"));
    }
    
    // Timeout prompt
    
    if($('body.Member_email').length){
        
        var docheck = true;
        var thisModal, lastChanceInterval;
        var promptWait = 2*60*1000;
        //var promptWait = 1*60*1000;
        var sessionTimeout = ($.fn.foreTeesSession("get","session_timeout") * 1000) - (promptWait + (30 * 1000));
        //var sessionTimeout = ((2 * 60) * 1000) - (promptWait + (30 * 1000));
        //console.log("Timeout:"+sessionTimeout);
        var sessionPing = function(){
            if(docheck && !lastChanceInterval){
                //console.log("PING!");
                $.ajax({  
                        url: 'ping',
                        data: {},
                        dataType: 'json',
                        success: function(data){
                            // data.user should contain user name
                            // data.club should contain club.
                            // Anything different, and we should probably let the user know somthing's wrong
                        },
                        error: function(xhr, e_text, e) {
                            // Couldn't ping.  could be a network error.
                        }
                    });
            } else {
                //console.log("SKIP PING!");
            }
            
        }
        $(document).idle({
            idle:sessionTimeout,
            onIdle: function(){
                if(!lastChanceInterval && docheck){
                    //console.log("Open Idle Modal");
                    $(document).foreTeesModal("alertNotice", {
                        width:500,
                        title:"Inactivity Timer",
                        message: 'You will be logged out of ForeTees in <span class="countDownTimer">02:00</span>, due to inactivity.',
                        continueButton:"Wait! I'm Here!",
                        allowClose: false,
                        allowContinue:true,
                        alertMode:false,
                        continueAction:function(modalObj){
                            modalObj.ftDialog("close");
                            clearInterval(lastChanceInterval);
                            lastChanceInterval = false;
                            sessionPing();
                        },
                        init:function(modalObj){
                            thisModal = modalObj;
                            lastChanceInterval = ftTimer({
                                timers:modalObj.find(".countDownTimer"),
                                time:promptWait,
                                //time:10*1000, // For testing
                                onTimeout:function(){
                                    docheck = false;
                                    clearInterval(lastChanceInterval);
                                    lastChanceInterval = false;
                                    modalObj.ftDialog("close");
                                    $(document).foreTeesModal("alertNotice", {
                                        width:500,
                                        title:"Session Expired",
                                        message: 'Sorry, your ForeTees session has expired due to inactivity.  To continue, you will need to log out and then back in to ForeTees.',
                                        allowClose: true,
                                        allowContinue:false,
                                        alertMode:false
                                    });
                                }
                            });

                        }
                    });
                } else {
                    //console.log("Still idle");
                }
            },
            onActive: function(){
                if(lastChanceInterval){
                    //clearInterval(lastChanceInterval);
                    //lastChanceInterval = false;
                    //thisModal.ftDialog("close");
                    //sessionPing();
                }
            },
            onPing: sessionPing
        });
    }
    
    
    // Cache ajax loader image
    var ftagaximg = new Image();
    ftagaximg.src = "/v5/assets/images/ajax-loader.gif";
    
    var defaultConTimes = $('.member_sheet_table').attr("data-ftDefaultContimes");
    $('.member_sheet_table .sS select').val(defaultConTimes); // Reset any pre-selected values for consecutive tee time select list
    
    $('#rwdNav .topnav_item a[href="Dining_slot?action=new"], #topnav a[href="Dining_slot?action=new"]').click(function(){
        ftUnHoverMenu();
        $('body').foreTeesModal('pleaseWait','open', true);
    });
    
    // Set-up lesson slot page completetion and redirect hacks -- will be used until Member_lesson is re-written to use new slot page.
    if(
        window.location.pathname.match(/.*\/Member_lesson/)
        ){
       //console.log("setting up return path");
        // We're on a Dining slot page
        var dflt = ftChangeFile( "Member_teelist" ); //  Where we'll go if we can't figure it out
        var rtUrl = ftGetUriParam('rtUrl');
        if(rtUrl == null){
            //console.log('user referrer');
            // First hit to the slot page.  Set return url the same as the referrer.
            rtUrl = document.referrer;
        }
        //console.log('found:'+rtUrl);
        
        if(!(
            // These are servlets that are O.K. to return to.
            rtUrl.match(/.*\/Member_announce/)
            ||
            rtUrl.match(/.*\/Member_teelist/)
            ||
            //rtUrl.match(/.*\/Member_sheet/)
            //||
            //rtUrl.match(/.*\/Member_select/)
            //||
            rtUrl.match(/.*\/Member_teelist_list/)
            ||
            (!window.location.pathname.match(/.*\/Member_lesson/)
            &&
            (
                rtUrl.match(/.*\/Member_events/)
            )
            )
        )){
        //if(rtUrl.match(/.*\/servlet\/Login/) !== null || rtUrl.match(/.*\/Dining_slot/) !== null || rtUrl == null || !rtUrl.length){
            // Got a problem.  referrer is not on whitelist
            //console.log('Unusable rtUrl passed');
            rtUrl = dflt;
        }
        rtUrl = ftSetJsid(rtUrl);
        //console.log('using:'+rtUrl);
        //console.log('return url='+rtUrl);
        //console.log('Overriding ftReturnToCallerPage');
        //console.log(ftReturnToCallerPage);
        ftReturnToCallerPage = function(){
            //console.log('returning to:'+rtUrl);
            //window.location = rtUrl;
            ft_historyBack(rtUrl);
        }
        
        
        
        var free_lesson = $('form[action^="Member_lesson"] input[name="cancel"]');
        if(free_lesson.length){
            
            var formValues = free_lesson.closest('form').find('input').serialize();
            //console.log("Values");
            //console.log(formValues);
            var skipUnload = false;
            var doUnload = function(async){
                $.ajax({
                    type: 'POST',
                    url: "Member_lesson",
                    data: formValues,
                    async: async,
                    dataType: 'text',
                    success: function(data){
                        skipUnload = true;
                        //console.log("did unload");
                        //console.log(formValues);
                        //console.log(data);
                    },
                    error: function(xhr){
                        //console.log("no unload");
                    }
                });
            }
            
            //ftFreeLessonOnUnload = true;
            //console.log("binding unload");
            //ftFreeLessonOnUnloadformValues = free_lesson.first().closest("form").serialize() + "&cancel";
            //console.log(ftFreeLessonOnUnloadformValues);
            ftOnSlotExit({
                onUnload:function(){
                    //console.log("On UNLOAD");
                    //console.log(obj);
                    if(!skipUnload){
                        doUnload(false);
                    }
                },
                onExit:function(){
                    //console.log("On EXIT");
                    //console.log(obj);
                    doUnload(false);
                },
                onBeforeUnload:function(){
                    if(!skipUnload){
                        //console.log("On ONBEFORE");
                        return "You are about leave without saving your data?";
                    }
                },
                navagateAwayMessage:"Are you sure you want to leave this page without saving your data?"
            });
            
            ftTimer({
                timers:$(".slot_timer"),
                time:6*60*1000,
                //time:10*1000, // For testing
                onTimeout:function(){
                    doUnload(true);
                    $(document).foreTeesModal("alertNotice", {
                        width:500,
                        title:"Reservation timer expired",
                        message: 'Sorry, your reservation timer has expired.<br><br>Please select "Continue" to return and select another time.',
                        continueButton:"Continue",
                        allowClose: false,
                        allowContinue:true,
                        alertMode:false,
                        continueAction:function(){
                            $('body').foreTeesModal("pleaseWait","open");
                            var canform = $("form[name=can]");
                            if(canform.length){
                                canform.submit();
                            }else{
                                ft_historyBack(rtUrl);
                            }
                        }
                    });
                }
            });
         
            $('form[action^="Member_lesson"]').submit(function(){
                // dont automatically free lesson if a form is submitted
                skipUnload = true;
            });
      
        }

        
        
        //Redirect completions for Member lesson
        $('.follow_return_path').click(function(e){
            ft_historyBack(rtUrl);
            e.preventDefault();
        });
        $('.auto_click_in_5').first().each(function(){
            setTimeout(ft_historyBack(rtUrl),5000);
        });
        $('form[action^="Member_lesson"]').each(function(){
            $(this).attr('action', ftUpdateQueryString("rtUrl", rtUrl, $(this).attr('action'), true));
        });
        $('form[action^="Member_teelist"]').submit(function(e){
            ft_historyBack(rtUrl);
            e.preventDefault();
        });
        $('a[href^="Member_lesson"]').each(function(){
            $(this).attr('href', ftUpdateQueryString("rtUrl", rtUrl, $(this).attr('href'), true));
            if(ftGetUriParam('event_popup') != null && document.referrer.match(/^http[s]*:\/\/[^\.]*\.*foretees.com\//i)){
                $(this).attr('target','_parent');
                //console.log("dr:"+document.referrer);
            }
        });

    }

    // Do workaround fo third party cookie blocking in Safari
    if($.fn.foreTeesSession("get","sso_tpa_mode") == "auth"){
        // verifyMem has detected that cookies are not being used
        // We'll ask our host application (premier) to "visit" foretees's thirdPartyAuth
        // servlet in the parent window, marking foretees as visited, 
        // so we can use cookies for foretees in an iframe
        ftPostMessage(parent,"ftTPA",{});
        return;
    }
    if(ftGetUriParam('sso_tpa_url') != null){
        // We've established a session cookie in the top/parent window.
        // Redirect back to the calling application allowing it to continue
        // using foretees.com in an iframe.
        var url = ftGetUriParam('sso_tpa_url');
        window.location.replace(url);
        return;
    }
    //if($.fn.foreTeesSession("get","sso_tpa_mode") ){
        //console.log($.fn.foreTeesSession("get","sso_tpa_mode"));
    //}
    //console.log("testing...");
    
    $("a.ftCsLink").click(function(e){
        
        var el = $(this);
        var data;
        try {
            data = JSON.parse(el.attr("data-ftjson"));
        //// console.log('Parsed Json');
        } catch (e) {
            //// console.log('Bad Json');
        }
        $('body').foreTeesModal('pleaseWait','open',true);
        if(data){
            // not yet complete
        } else {
            window.location.href = el.attr("data-fthref");
        }
        e.preventDefault();
    });
    
    $(".submitForm").click(function(e){
        $('body').foreTeesModal('pleaseWait','open',true);
        var el = $(this);
        
        if(el.attr("target")){
            //console.log("submitting:"+el.attr("target"));
            $(el.attr("target")).submit();
        } else {
            //console.log("submitting closest");
            el.closest('form').submit();
        }     
        e.preventDefault();
    });
    
    
    // fix member side menus on IE10 w/ touch capabilities
    // msMaxTouchPoints is IE only and only present if documentMode is 10, ie. ie10 in ie9 mode doesn't have it and won't need the fix
    // for now let's use the documentMode (IE only and we only need this fix if in that mode - doctype should keep us out of quirks mode)
    /*
    if ($.browser.msie && document.documentMode == 10) { // && $.browser.version == 10 && window.navigator.msMaxTouchPoints
        $("#topnav>ul>li").each(function(index){
            if($(this).find('ul').length){
                $(this).attr('aria-haspopup','true');
            }
        });

        $("#topnav>ul>li>ul").attr('aria-haspopup','false');
    }
    */
    // IE 6/7 fixes
    //if ($.browser.msie && $.browser.version <= 7) {

    // This has moved to ie7_compatibility.js  

    //}

    /*
    // Find buttons with a darker background colors
    $(".standard_button").each(function(){
        //console.log($(this).html() + ":" + $(this).css('backgroundColor'));
        var threshold = 9;
        var rgb = $(this).css('backgroundColor').match(/^rgb\((\d+),\s*(\d+),\s*(\d+)/);
        var R = parseInt(rgb[1]);
        var G = parseInt(rgb[2]);
        var B = parseInt(rgb[3]);
        var luminance = Math.sqrt( 0.241*R^2 + 0.691*G^2 + 0.068*B^2 );
        //console.log("luminance:" + luminance);
        if(luminance < threshold){
            //console.log("dark_background");
            $(this).addClass("dark_background");
        }     
    });
     */
    
    // Fix width of standard_legend_list
    $("fieldset.standard_legend_list > div > div").each(function(){
        var child = $(this).find("p").first();
        if(child.length){
            var cur_width = child.width();
            $(this).addClass("width_test");
            if(child.width() > cur_width){
                $(this).addClass("double_width");
            }
            $(this).removeClass("width_test");
        }
    });
    
    // Activate print buttons
    $(".print_button").click(function(){
        window.print();
        return false;
    });
    
    ftFixCalendarCells();

    // Run server clock, if any
    $(".jquery_server_clock").foreTeesServerClock();

    // Display member calendars
    $(".calendar.member").foreTeesMemberCalendar({
        courseSelect:$('select[name="course"]'),
        reloadObject:$("#refresh a.standard_button")
    });
    
    // Display individual lesson calendars
   // $(".calendar.individual_lesson").foreTeesMemberCalendar();
    
    // Display lesson calendars
    $(".calendar.individual_lesson").foreTeesMemberCalendar({
        toolTips: {
            1:'Space Available'
        },
        onSelect: function(dateText,inst){
            var f = $("form[name=pform]");
            //console.log(f.attr("name"));
            var cd = f.find("input[name=calDate]")
            if(!cd.length){
                cd = $('<input type="hidden" name="calDate">');
                f.append(cd);
            }
            cd.val(dateText);
            f.submit();
        },
        jsonSourceUrl:"nothing", // no ajax for this calendar
        disableOnDayValue:"ft_code_0" // Disable any "0" day
    });

    $(".calendar.lesson").foreTeesMemberCalendar({
        toolTips: {
            1:'Space Available'
        },
        onSelect: function(dateText,inst){
            var f = document.forms["frmLoadDay"];
            f.calDate.value = dateText;
            f.submit();
        },
        jsonSourceUrl:"nothing", // no ajax for this calendar
        disableOnDayValue:"ft_code_0" // Disable any "0" day
    });

    // Display flxrez sheet calendars
    $(".calendar.flxsheet").foreTeesMemberCalendar({
        toolTips: {
            1:'Times Available'
        },
        onSelect: function(dateText,inst){
            var f = document.forms["frmLoadDay"];
            f.calDate.value = dateText;
            f.submit();
        },
        jsonSourceUrl:"nothing" // no ajax for this calendar
    });
    
    // Activate date pickers
    $("input.ft_date_picker").each(function(){
        var options = {
            showOn: "both",
            buttonImage: "../assets/images/calendar_select.png"
        };
        if($(this).attr("data-ftstartdate")){
            options.minDate = ftStringDateToDate($(this).attr("data-ftstartdate"));
        }
        if($(this).attr("data-ftenddate")){
            options.maxDate = ftStringDateToDate($(this).attr("data-ftenddate"));
        }
        if($(this).hasClass("ft-date-picker-dining")){
            options.onSelect = function(dateText) {
                sd(dateText);
            };
        }
        $(this).datepicker(options);
        $(this).attr( 'readonly' , 'true' );
    });
    

    // Activate Wait-list buttons on the member tee sheet
    $(".button_legend_waitlist,.waitlist_image_link, .waitlist_button").click(function(e){
            
        $(this).foreTeesModal("waitListPrompt");
        e.preventDefault();
        
    });
    
    // Activate lottery buttons on the member tee sheet
    $(".lottery_button,.button_legend_lottery").click(function(e){
        
        $(this).foreTeesModal("lotteryPrompt");
        e.preventDefault();
        
    });
    
    // Jump to position in page
    var ftJumpPosition = getParameterByName("jump");
    if(ftJumpPosition != null && parseInt(getParameterByName("jump"), 10) > 0){
        // set hash name in teesheet, if there is one
        $("table.member_sheet_table tr:nth-child("+ftJumpPosition+")").each(function(){
            $(this).find("td:first-child").each(function(){
                $(this).append('<a name="jump_'+ftJumpPosition+'"></a>');
            }); 
        });
        // Jump to hash name
        window.location.hash = "jump_" + ftJumpPosition;
    }
    
    // Initialize compose email page
    $(".compose_email_container").foreTeesEmail();
    
    // Initialize distribution list page
    $(".manage_distribution_lists_container").foreTeesEmail("init",{
        mode:"distributionList"
    });
    
    // Initialize slot page
    // Do it in a set timeout to work-around an IE9 bug in premier mode
    setTimeout(function(){
        $(".slot_container").foreTeesSlot();
    },1);
    
    
    $(".post_button").click(function(index){
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
        // Create a "Please wait" modal, blocking user input while the form is submitting
        obj.foreTeesModal("pleaseWait","open",true);
        
        if(!json.method){
            json.method = "post";
        }
        
        json.url = ftSetJsid(json.url);

        // Build form, and post it.
        var formObj = $("<form action=\""+json.url+"\" method=\""+json.method+"\">" + ftObjectToForm(json.data, {}) + "</form>");
        $("body").append(formObj);
        formObj.submit();
            
        return false;
    });
    
    $(".htmlblock_button").click(function(index){
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
        obj.foreTeesModal("htmlBlock",json);
            
        return false;
    });
    
    $(".activity_button").click(function(index){
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
        // Create a "Please wait" modal, blocking user input while the form is submitting
        obj.foreTeesModal("pleaseWait","open",true);
            
        // Build form, and post it.
        var formObj = $('<form action="'+ftSetJsid("Member_activity_slot")+'" method="post">' + ftObjectToForm(json, {
            type:false
        }) + "</form>");
        $("body").append(formObj);
        formObj.submit();
        return false;
    });

/*
 
         // Find and bind to related consecutive times select list
        
 */

    // Activate Tee-Time consecutive times select list
    $(".member_sheet_table .sS select").change(function(){
        var parentRow = $(this).closest(".rwdTr");
        parentRow.find(".teetime_button").click();
    });

    // Activate Tee-Time buttons and select objects on the member tee sheet
    $(".teetime_button").click(function(){
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
        if(json.date){
            var slotDate = ftIntDateToDate(json.date);
            json.day = slotDate.format("dddd");
        }
        // Create a "Please wait" modal, blocking user input while the form is submitting
        obj.foreTeesModal("pleaseWait","open",true);

        // Add contimes to json object, if available
        var contimes = 1;
        var parentRow = $(this).closest(".rwdTr");
        var select = parentRow.find(".sS select").first();
        if(select.length){
            contimes = select.val();
        }
        var base_url = "";
        if(json.base_url){
            base_url = json.base_url;
        }
        json.contimes = contimes;
        // Build form, and post it.
        var formObj = $('<form action="'+base_url+ftSetJsid("Member_slot")+'" method="post">' + ftObjectToForm(json, {
            type:false,base_url:false
        }) + "</form>");
        $("body").append(formObj);
        formObj.submit();
        return false;
    });
    
    // Init any slideshows in the announcement page step 1 - Clean up slides
    $("div.announcement_container div.slide_container").each(function(){

        var slides = $(this).children("div:not(.slide_settings)");
        // Remove non-slides
        slides.each(function(index){
            // Remove empty slides
            
            if($.trim($(this).html()) == ""){
                $(this).remove();
            } else {
        //console.log("swa"+index+":"+$(this).width());
        }
        });
        // Set first slide visible
        slides.first().css("position","relative").css('visibility','visible');
        slides = $(this).children("div");
        if (!slides.length){
            // Remove empty slide shows
            $(this).remove(); 
        }
        
    });
    
    // Init any slideshows in the announcement page step 2 - Configure and display slideshow
    $("div.announcement_container div.slide_container").each(function(){
        
        var options = {
            fx:"fade",
            speed: 400,
            timeout: 4000,
            containerResize: 0
        }
        var show_navigation = false;
        var show_pager = false;
        var page_nav_class = "";
        var page_nav_style = "";
        var fx = 'fade', speed = 400, timeout = 4000;
        if($(this).children(".slide_settings").length){
            $(this).children(".slide_settings").each(function(){
                var settings = $(this).html().replace(/<\/?[^>]+>/gi, '');
                //console.log("foundset:"+settings);
                var optionsa = $.trim(settings).split(",");
                //console.log("foundopts:"+JSON.stringify(options));
                for(var i = 0; i < optionsa.length; i++){
                    var optiona = $.trim(optionsa[i]).split(":");
                    //console.log("foundopt:"+JSON.stringify(option));
                    
                    if(optiona.length == 2){
                        switch($.trim(optiona[1])){
                            case "true":
                                optiona[1] = true;
                                break;
                            case "false":
                                optiona[1] = false;
                                break;
                            default:
                                optiona[1] = $.trim(optiona[1]);
                                break;
                        }
                        switch($.trim(optiona[0])){
                            case "transition-effect":
                                options.fx = optiona[1];
                                break;
                            case "transition-speed":
                                options.speed = optiona[1];
                                break;
                            case "display-length":
                                options.timeout = optiona[1];
                                break;
                            case "display-navigation":
                                show_navigation = optiona[1];
                                break;
                            case "display-progress":
                                show_pager = optiona[1];
                                break;
                            case "nav-box-class":
                                page_nav_class = optiona[1];
                                break;
                            case "nav-box-style":
                                page_nav_style = optiona[1];
                                break;
                            case "":
                                break;
                            default:
                                options[$.trim(optiona[1])] = optiona[1];
                                if(!isNaN(optiona[1])){
                                    if(parseFloat(optiona[1]) == parseInt(optiona[1], 8)){
                                        optiona[1] = parseInt(optiona[1], 8);
                                    } else {
                                        optiona[1] = parseFloat(optiona[1]);
                                    }
                                }
                                break;
                        }
                    }
                }
            });
        }
        
        var height = $(this).height();
        var width = $(this).width();
        
        //console.log("cw:"+width);
        
        $(this).children(":not(div),.slide_settings").remove();
        var slides = $(this).children("div");

        slides.each(function(index){
            // Remove empty slides
            $(this).width(width);
            //console.log("sw"+index+":"+$(this).width());
            if($.trim($(this).html()) == ""){
                $(this).remove();
            }
            if($(this).height() > height){
                height = $(this).height();
            }
        });
        //if(false){
        if(slides.length){
            $(this).height(height);
            $(this).width(width);
            slides.height(height);
        
            $(this).wrap('<div class="slideshow_parent"></div>');
            var nav_page_container = $('<div class="slideshow_pager_nav_container '+page_nav_class+'" style="'+page_nav_style+'"><div class="slideshow_pager_nav_box"></div></div>');
            var nav_page_box = nav_page_container.find(".slideshow_pager_nav_box");
            if(show_pager || show_navigation){
                if(page_nav_class.search(/(^| )inside($| )/) > -1){
                    $(this).before(nav_page_container);
                    $(this).parent().css("position", "relative");
                    nav_page_container.width(width);
                } else {
                    if(page_nav_class.search(/(^| )top($| )/) > -1){
                        $(this).before(nav_page_container);
                    } else {
                        $(this).after(nav_page_container);
                    }
                }
            }
            if(show_pager){
                var pager_container = $('<div class="slideshow_pager_container"></div>');
                options.pager = pager_container;
                options.pagerAnchorBuilder = function (i, el){
                    return '<a href="#" class="small_dot item'+(i+1)+'"><b>Item '+(i+1)+'</b></a>';
                }
                nav_page_box.append(pager_container);
            }
            if(show_navigation){
                var nav_container = $('<div class="slideshow_nav_container"><a href="#" class="nav_left"><b>Prev</b></a><a href="#" class="nav_pause_play"><b>Pause/Play</b></a><a href="#" class="nav_right"><b>Next</b></a></div>');
                var prev = nav_container.find("a.nav_left");
                var next = nav_container.find("a.nav_right");
                var pause_play = nav_container.find("a.nav_pause_play");
                options.next = next;
                options.prev = prev;
                pause_play.data("ft-slideShowObj", $(this));
                pause_play.click(function(){
                    var obj = $(this).data("ft-slideShowObj");
                    obj.cycle('toggle');
                    if(obj[0].cyclePause){
                        $(this).addClass('paused');
                    }else{
                        $(this).removeClass('paused');	
                    }
                    return false;
                });
                nav_page_box.append(nav_container);
            }
            $(this).cycle(options);
            slides.css('visibility','visible');
        }else {
            $(this).css('display','none'); 
        }
        
    });
    
    // ForeTees Connect Premier
    // do this last after all other onload tasks

    if (parent != window  && $.fn.foreTeesSession("get","premier_referrer")) {
        // we are running in a frame so append our resize helper and call it
    	ftResizeIframe();
        //console.log('history:'+history.length);
        if(document.referrer && document.referrer.match(/foretees\.com:*[0-9]*$/i) == null){
            //we were probably called by premier
            if(navigator.userAgent.toLowerCase().indexOf('firefox') > -1){
                // if firefox, we need to jump back an extra page
                ftBackCount ++;
            }
        }
        switch ($.ftQueryString['iframe_action']){
            case 'close':
                ftBackCount ++;
                break;
        }
        // Check if content has changed and adjust if needed.
        setInterval(ftCheckIframeSize,100);
        
    }
    
    // Dining event modals
    $("a.dining_event_modal").click(function(event){
        var o = $(this);
        var link = o.attr("data-ftlink");
        var title =  o.text();
        o.foreTeesModal("linkModal",{
            link:link,
            title:title
        });
        return false;
    });
    
    $('.ftBackButton').click(function(e){
        ft_historyBack();
        e.preventDefault();
    });

    ftActivateElements($("body"));


    // Scroll to Top
    // add pages here that we want to prevent scrollUp from activating
    if($('.slot_container').length){
        ftScrollUtilize = false;
    }
    
    if(ftScrollUtilize == true) {
        $("html").append("<a href=\"javascript:void(0);\" id=\"scrollUp\" style=\"display: none;\"></a>");
        $('#scrollUp').click(function(event) {
            event.preventDefault();
            $('html, body').animate({scrollTop: 0}, ftScrollDuration);
            return false;
        })
    }  
    
    if($.fn.foreTeesSession("get","rwd") == false && ftScrollUtilize == true){
        //console.log("activating scrollUp on desktop page");
        $(window).scroll(function() {
            if ($(this).scrollTop() > ftScrollActivationOffset) {
              $('#scrollUp').fadeIn(ftScrollDuration);
            } else {
              $('#scrollUp').fadeOut(ftScrollDuration);
            }
        });
    }

// End of jQuery on-load config
});