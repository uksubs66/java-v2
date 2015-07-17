/* 
 * Initialization for all devices
 */


/*************************************************************
 *
 * Initialize jQuery objects
 *  
 **************************************************************/
// jQuery start on DOM ready
$(document).ready(function() {

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
    
    // Control Sorting and deleting in Member_partner
    if($.fn.sortable){
        $( "ul.partnerList" ).sortable( {
            handle: ".sortHandle",
            axis: "y",
            start: function( event, ui) {
                //var el = $(this);
                //ui.data('ftLastIndex', ui.index())
                //console.log('start');
              //  console.log(ui);
            },
            update: function( event, ui ) {
               // console.log('update');
              //  console.log(ui);
                var emsg = 'Unable to change order. Your session may have expired.';
                var box = ui.item.closest('ul');
                var data = {json:true,submitOrder:true};
                data.user_id = box.attr('data-ftUserId');
                data.activity_id = box.attr('data-ftActivityId');
                box.find('li').each(function(i){
                    //console.log($(this).attr('data-ftPartnerId')+':'+i);
                    data['priority_'+$(this).attr('data-ftId')] = i+1;
                });
                //$('body').foreTeesModal("pleaseWait");
                $.ajax({
                    type: 'POST',
                    url: "Member_partner",
                    data: data,
                    dataType: 'json',
                    success: function(r){
                        //console.log('this');
                        //    console.log($(this));
                        //$('body').foreTeesModal("pleaseWait","close");
                        if(!r.result){
                            //console.log(r);
                            alert(emsg);

                            ///ui.sortable.animate(ui.sortable.data().ftLastPos,"slow");
                        }
                        //console.log("did unload");
                        //console.log(data);
                    },
                    error: function(xhr){
                        //$('body').foreTeesModal("pleaseWait","close");
                        //console.log(xhr);
                        alert(emsg);
                        //ui.sortable.animate(ui.sortable.data().ftLastPos,"slow");
                    }
                });
            }
        } );
    }
    
    $( "ul.partnerList a.deleteButtonSmall").click( function(){
        var li = $(this).closest('li');
        li.addClass('deleting');
        var emsg = 'Unable to remove partner. Your session may have expired.';
        var box = li.closest('ul');
        var data = {json:true,removePartner:true};
        data.user_id = box.attr('data-ftUserId');
        data.activity_id = box.attr('data-ftActivityId');
        data.partner_id = li.attr('data-ftPartnerId');
        //$('body').foreTeesModal("pleaseWait");
        $.ajax({
            type: 'POST',
            url: "Member_partner",
            data: data,
            dataType: 'json',
            success: function(r){
                //$('body').foreTeesModal("pleaseWait","close");
                if(!r.result){
                    //console.log(r);
                    alert(emsg);
                    li.removeClass('deleting');
                } else {
                    li.animate({
                        height:'0px'
                    },
                    {
                        duration: 250,
                        complete: function(){
                            $(this).remove();
                        }
                    });
                }
                //console.log("did unload");
                //console.log(data);
            },
            error: function(xhr){
                //$('body').foreTeesModal("pleaseWait","close");
                //console.log(xhr);
                alert(emsg);
                li.removeClass('deleting');
            }
        });
        return false;
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
    
    // Find calendar cells with too much data
    $("table.list_calendar").each(function(index){
        
        var parent_table = $(this); 
        
        $(this).find("td div.day_wrapper").each(function(index){
            
            var wrapper = $(this);
            var container = $(this).find("div.day_container").first();
            
            var parent_cell = container.parents("td").first();
            var width = container.width();
            //var height = container.width();
            
            parent_cell.addClass("find_overflow");
   
            var overflow_width = container.width();
            var overflow_height = container.outerHeight();
            
            //wrapper.removeClass("find_overflow");
            parent_cell.removeClass("find_overflow");
            //container.find(".ical_button + a, .ical_button + div").removeClass("find_overflow");
            
            /*
            var wrapper_height = parent_cell.height() +1;
            if(parent_table.data("ft-firstCellHeight") !== undefined){
                wrapper_height = parent_table.data("ft-firstCellHeight");
            } else {
                parent_table.data("ft-firstCellHeight", wrapper_height);
            }
            */
            var wrapper_height = wrapper.height();
            var wrapper_width = width; //parent_cell.width() + 1;
            wrapper.width(wrapper_width);
            //wrapper.height(wrapper_height);

            //console.log("found:"+index+":"+width +":" + overflow_width +"/"+height +":" + overflow_height);
        
            //parent_cell.attr("data-debug","ow:"+overflow_width+" oh:"+overflow_height+" w:"+wrapper_width+" h:"+wrapper_height);
        
            if(wrapper_width < overflow_width || wrapper_height < overflow_height){
            
                if(wrapper_width > overflow_width){
                    overflow_width = wrapper_width;
                }
                if(wrapper_height > overflow_height){
                    overflow_height = wrapper_height;
                }
            
                if(overflow_width > wrapper_width){
                //overflow_width += 3;
                }
            
                wrapper.data("ft-cellSize", {
                    overflow_width:overflow_width,
                    overflow_height:overflow_height,
                    wrapper_height:wrapper_height,
                    wrapper_width:wrapper_width
                
                });
            
                var more_mask = $('<a class="more_mask"><b>...more</b></a>');
                wrapper.prepend(more_mask);
            
                if(!ftIsMobile()){
                    more_mask.hover(
                        function(event){
                            $(this).click();
                        },
                        function(event){
                        });
                }

                more_mask.click(function(event){
                    clearTimeout($(this).data("ft-showTimer"));
                    event.stopPropagation();
                    var wrapper = $(this).parent();
                    var data = wrapper.data("ft-cellSize");
                    var parentTable = $(this).parents("table").first();
                    parentTable.addClass("ie7_zindex_fix");
                    //$(this).find(".more_mask").hide();
                    wrapper.addClass("cal_expand");
                    var target = wrapper.find(".day_container");
                    //var offset = target.offset();
                    target.addClass("cal_expand"); //.offset(offset);
                    //target.width(data.overflow_width);
                    //target.height(data.overflow_height);
                    target.width(data.wrapper_width);
                    target.height(data.wrapper_height);
                    target.css('left','');
                    target.css('top','');
                    var windowWidth = $(window).innerWidth();
                    var windowHeight = $(window).innerHeight();
                    if(ftIsMobile("ios") || ftIsMobile("android") || ftIsMobile("blackberry")){
                        windowWidth = window.innerWidth;
                        windowHeight = window.innerHeight;
                    }
                    //console.log(target.position());
                    var offset = target.offset();
                    var offsetLeft = target.position().left;
                    var offsetTop = target.position().top;
                    if(offset.left + data.overflow_width + 10 > (windowWidth+$(window).scrollLeft())){
                        offsetLeft += (windowWidth+$(window).scrollLeft()) - (offset.left + data.overflow_width + 10);
                    }
                    if(offset.top + data.overflow_height + 10 > (windowHeight+$(window).scrollTop())){
                        offsetTop += (windowHeight+$(window).scrollTop()) - (offset.top + data.overflow_height + 10);
                    }
                    target.animate(
                    {
                        left: offsetLeft,
                        top: offsetTop,
                        width: data.overflow_width,
                        height: data.overflow_height
                    },
                    {
                        duration: 100
                    }
                    
                    );
                    return false;
                });
            
            
            
                wrapper.hover(
                    // in
                    function(event){
                    /*
                    event.stopPropagation();
                    var wrapper = $(this);
                    var data = wrapper.data("ft-cellSize");
                    //$(this).find(".more_mask").hide();
                    wrapper.addClass("cal_expand");
                    var target = wrapper.find(".day_container");
                    //var offset = target.offset();
                    target.addClass("cal_expand"); //.offset(offset);
                    target.width(data.overflow_width);
                    target.height(data.overflow_height);
                    return false;
                    */
                    },
                    // Out
                    function(){
                        var data = $(this).data("ft-cellSize");
                        //$(this).find(".more_mask").hide();
                        var target = $(this).find(".day_container");
                        //var offset = target.offset();
                        var parentTable = $(this).parents("table").first();
                        target.stop(true);
                        $(this).removeClass("cal_expand");
                        
                        parentTable.removeClass("ie7_zindex_fix");
                        target.removeClass("cal_expand"); //.offset(offset);
                        target.width(data.wrapper_width);
                        target.height(data.wrapper_height);
                        target.css('left','');
                        target.css('top','');
        
                    });
            }
        
        });
        
    });

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
    $(".button_legend_waitlist,.waitlist_image_link, .waitlist_button").click(function(){
            
        $(this).foreTeesModal("waitListPrompt");
        return false;
        
    });
    
    // Activate lottery buttons on the member tee sheet
    $(".lottery_button,.button_legend_lottery").click(function(){
        
        $(this).foreTeesModal("lotteryPrompt");
        return false;
        
    });
    
    // Activate event buttons on the member tee sheet
    $(".button_legend_event, .event_button").click(function(){
            
        $(this).foreTeesModal("eventPrompt");
        return false;
        
    });
    
    // Weather widget
    $('div.announcement_container div.weather_widget_accuweather_wide').first().each(function(){
        setTimeout(
        function(){
            $('div.announcement_container div.weather_widget_accuweather_wide').first().each(
            function(){
                var obj = $(this).find("#NetweatherContainer");
                var zipcode = $.fn.foreTeesSession("get","zipcode");
                var weatherObj = $('<script src="http://netweather.accuweather.com/adcbin/netweather_v2/netweatherV2ex.asp?partner=netweather&tStyle=whteYell&logo=1&zipcode='+zipcode+'&lang=eng&size=11&theme=blue&metric=0&target=_blank"></script>');
                obj.append(weatherObj);
        })}, 250);
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
    $(".slot_container").foreTeesSlot();
    
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
    $(".member_sheet_table tr select").change(function(){
        var parentRow = $(this).parents("tr").first();
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
        var parentRow = $(this).parents("tr").first();
        var select = parentRow.find("select").first();
        if(select.length){
            contimes = select.val();
        }
        json.contimes = contimes;
        // Build form, and post it.
        var formObj = $('<form action="'+ftSetJsid("Member_slot")+'" method="post">' + ftObjectToForm(json, {
            type:false
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

// End of jQuery on-load config
});