// Default settings (use club javascript files to override)


var ftMessage = {
    ajaxError:"<b>Error retrieving data.</b>  Your session may have timed out.  Please log back in and try again.  If this problem continues, please contact support."
}

// Polyfills

Number.prototype.formatMoney = function(c, d, t){
    var n = this, 
    c = isNaN(c = Math.abs(c)) ? 2 : c, 
    d = d == undefined ? "." : d, 
    t = t == undefined ? "," : t, 
    s = n < 0 ? "-" : "", 
    i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", 
    j = (j = i.length) > 3 ? j % 3 : 0;
    return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
};

if (!Array.prototype.forEach) {
    Array.prototype.forEach = function(callback){
      for (var i = 0; i < this.length; i++){
        callback.apply(this, [this[i], i, this]);
      }
    };
}

if (!String.prototype.trim) {
  (function() {
    // Make sure we trim BOM and NBSP
    var rtrim = /^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g;
    String.prototype.trim = function() {
      return this.replace(rtrim, '');
    };
  })();
}

// From https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/keys
if (!Object.keys) {
  Object.keys = (function() {
    'use strict';
    var hasOwnProperty = Object.prototype.hasOwnProperty,
        hasDontEnumBug = !({toString: null}).propertyIsEnumerable('toString'),
        dontEnums = [
          'toString',
          'toLocaleString',
          'valueOf',
          'hasOwnProperty',
          'isPrototypeOf',
          'propertyIsEnumerable',
          'constructor'
        ],
        dontEnumsLength = dontEnums.length;

    return function(obj) {
      if (typeof obj !== 'object' && (typeof obj !== 'function' || obj === null)) {
        throw new TypeError('Object.keys called on non-object');
      }

      var result = [], prop, i;

      for (prop in obj) {
        if (hasOwnProperty.call(obj, prop)) {
          result.push(prop);
        }
      }

      if (hasDontEnumBug) {
        for (i = 0; i < dontEnumsLength; i++) {
          if (hasOwnProperty.call(obj, dontEnums[i])) {
            result.push(dontEnums[i]);
          }
        }
      }
      return result;
    };
  }());
}

//

if ( $.mobile ) {
    // If using jquery mobile
    $.fn.ftDialog = $.fn.popup;
} else {
    //console.log($.fn.dialog);
    $.fn.ftDialog = $.fn.dialog;
}

var ftOrientationWidth = $(window).width();
ftIsIe8 = false;
 
$(window).on('resize',function(){
    if(!ftIsMobile()){
        $(window).trigger('ft-resize');
    } else if(!window.orientationchange && ($(window).width() != ftOrientationWidth)) {
        ftOrientationWidth = $(window).width();
        $(window).trigger('ft-resize');
    }
});

$(window).on('orientationchange',function(){
    if(ftIsMobile() && window.orientationchange){
        $(window).trigger('ft-resize');
    }
});




var ftAjaxCallHistory = {};
var ftAjaxCallHistoryCounter = 0;
var ftAjaxCallHistoryLastUuid = '';
var ftBackCount = 1; // how far to go back in history on "back".
var ftIframeHeight = 0,ftIframeWidth = 0, ftDoIframeCheck = false;
var parentSize = {offsetTop:0, scrollTop:0, height:0};
//var iFramePad = 40;
var iFramePad = 0;
var ftModalPad = 0; //used by modals to pad body if needed 
var ftModalPads = {};
// scroll to top (these are used in the inits)
var ftScrollActivationOffset = $(window).height() / 2;// 325; // set to 3/4 the viewport?
var ftScrollDuration = 250;
var ftScrollUtilize = true; // default to on and we'll disable for specific pages in init-global

// Global vars
var ftVar = {
    resizeTimer:null
}; 

$(document).ajaxSend(
    function(event, jqXHR, ajaxSettings){
        ajaxSettings.ft_debug_tracker_uuid = "uid:" + new Date().getTime() + ":" + ftAjaxCallHistoryCounter;
        ftAjaxCallHistoryCounter ++;
        ftAjaxCallHistoryLastUuid = ajaxSettings.ft_debug_tracker_uuid;
        ftAjaxCallHistory[ajaxSettings.ft_debug_tracker_uuid] = {settings:ajaxSettings, start_time:(new Date())};
        //console.log("NewAjaxHistory:"+ajaxSettings.ft_debug_tracker_uuid); 
    }
);
$(document).ajaxComplete(
    function(event, jqXHR, ajaxSettings){
        if(ajaxSettings.ft_debug_tracker_uuid && ftAjaxCallHistory[ajaxSettings.ft_debug_tracker_uuid]){
            ftAjaxCallHistory[ajaxSettings.ft_debug_tracker_uuid].xhr = jqXHR;
            ftAjaxCallHistory[ajaxSettings.ft_debug_tracker_uuid].end_time = (new Date());
            if(ajaxSettings.ft_debug_tracker_uuid >= ftAjaxCallHistoryLastUuid){
                ftAjaxCallHistoryLastUuid = null;
            }
            //console.log("AddAjaxHistory:"+ajaxSettings.ft_debug_tracker_uuid); 
        }
    }
);

// append jsessionid to ajax calls, if we're in an iframe
$.ajaxSetup({
    timeout:60000, 
    cache:false,
    beforeSend: function(e, d){
        d.url = ftSetJsid(d.url);
    }
});  // Default to 60 second timeout for ajax calls in an attempt to narrow down IE "please wait" issues.

// Append jsession id
function ftSetJsid(url){
    /*
    var jsid = $.fn.foreTeesSession("get","jsid");
    if(typeof url == "string" && jsid && window != parent){
        var urla = url.split("?");
        var urla2 = urla[0].split("#");
        //console.log("Url test:"+urla);
        // Verify it's a foretees servlet link, and doesn't have the jsessionid set already
        if(
            !url.match(/[\?;&]jessionid=/) 
            &&
            !url.match(/^#$/) 
            &&
        (
            urla2[0].match(/^[^\/]*$/) || 
            //urla2[0].match(/^\.\.\/[^\/]*$/) || 
            urla2[0].match(/^\.\.\/[^_\/]+_[^_\/]+\/[^\/]+$/) || 
            urla2[0].match(/\/v5\/[^_\/]+_[^_\/]+\/[^\/]+$/)
        )){
            //console.log("servlet found");
            // If so, insert jsession id
            urla2[0] += ";jsessionid=" + jsid;
            if(urla.length > 1){
                urla2[0] += "?" + urla[1];
            }
            if(urla2.length > 1){
                urla2[0] += "#" + urla2[1];
            }
            url = urla2[0];
        }else{
            //console.log("not fturl:"+url);
        }
    }
    */
    return url;
}


/*************************************************************
 *
 * Empty "unload" event to invalidate cache on back button 
 * in safari, and possibly other browsers
 *  
 **************************************************************/
$(window).unload(function(){});

/*************************************************************
*
* Global Javascript Functions
*
*************************************************************/

// postMessage handeler

function ftMessageReceiver(e) {
    ftDoIframeCheck = true; // Turn back on resize polling
    var data = {};
    try{
        data=JSON.parse(e.data);
    }catch(e){
        data={};
    }
    //console.log('IframePostMessage:');
    //console.log(data);
    if(data.command){
        switch (data.command) {
            case 'ftParentSize':
                parentSize = data.message;
                break;
            case 'ftRequestResize':
                //console.log('requested resize');
                ftResizeIframe();
                break;
            case 'ftDidResize':
                // Tell all modals to resize and reposition
                $(window).trigger('ftModalForceCenter');
                break;
        }
    }
}

function ftPostMessage(win,command,message){
    if(win && win.postMessage){
        ftDoIframeCheck = false; // turn off resize polling
        //console.log("Sending Post Message:"+command);
        win.postMessage(JSON.stringify({command:command,message:message}),'*');
    } else {
        // No support for post message -- do it via iframe (IE7 fallback)
        var url = $.fn.foreTeesSession("get","premier_referrer");
        if(url && command){
            var domain = url.match(/:\/\/(.[^/]+)/)[1];
            var isforetees = (domain.match(/foretees\.com:*[0-9]*$/i) != null);
            if(isforetees){
                // Probably not cross-domain.
            } else {
                var pipe = $('iframe#helpframe');
                var cmd_uri = 'flexIframeResize.html?pm=' + encodeURIComponent(JSON.stringify({command:command,message:message}));
                var new_url = 'http://' + domain + '/fw/_js/'+cmd_uri+'&cacheb='+Math.random();
                 ftDoIframeCheck = false; // Stop iFrame size check interval from triggering another change
                 if(!pipe.length){
                     pipe = $('<iframe style="display:none;" id="helpframe" src="'+new_url+'" height="0" width="0" frameborder="0"></iframe>');
                     pipe.load(function(){                         
                         // iFrame has loaded, so allow iFrame check interval to make changes again.
                        ftDoIframeCheck = true;
                        switch(command){
                            case 'ftResizeIframe':
                                // Force any open modals to adjust to the new size
                                // Since we're using IE7, and wont recive a didResize callback
                                 $(window).trigger('ftModalForceCenter');
                                 break;
                        }
                     });
                     $("body").append(pipe);
                 } else {
                     pipe.get(0).contentWindow.location.replace(new_url);
                     //console.log('height_: '+height);
                 }
            }
        }
    }
}

// Common dynamic form elements
function ftActivateElements(obj){
    // Activate date-picker
    //console.log('DP check...');
    obj.find("input.ft_date_picker_field").not(".hasDatePicker").each(function(){
        //console.log('DP set...');
        var el = $(this);
        var options = {
            showOn: "both",
            changeMonth: true,
            changeYear: true,
            format: 'mm/dd/yyyy',
            buttonImage: "../assets/images/calendar_field.png",
            onSelect: function(){
                $(this).change();
            },
            showAnim:'', // This is needed! (date picker animations conflict with jquery.animate-enhanced)
            showButtonPanel: true,
            closeText: 'Close',
            currentText: '',
            beforeShow:function(){
                // make sure the date picker shows over other items
                setTimeout(function(){$('.ui-datepicker').css('z-index',3000);},0);
            }
        };
        el.mask("99/99/9999");
        if(el.attr("data-ftstartdate")){
            options.minDate = ftStringDateToDate($(this).attr("data-ftstartdate"));
        }
        if(el.attr("data-ftenddate")){
            options.maxDate = ftStringDateToDate($(this).attr("data-ftenddate"));
        }
        if(el.attr("data-ftdefaultdate")){
            options.defaultDate = ftStringDateToDate($(this).attr("data-ftdefaultdate"));
        }
        el.datepicker(options);
        el.datepicker("widget")
            .addClass("calendar");
            //.addClass("aboveMenu");
        el.attr("readonly","true");
        el.change(function(){
            var dp = $(this);
            var curDate = dp.datepicker("getDate");
            var maxDate = new Date(dp.datepicker("option","maxDate"));
            var minDate = new Date(dp.datepicker("option","minDate"));
            if(isNaN(Date.parse(dp.val()))){
                if(dp.data("ftLastGoodDate")){
                    dp.datepicker("setDate", dp.data("ftLastGoodDate"));
                } else if(dp.datepicker("option","maxDate")){
                    dp.datepicker("setDate", maxDate);
                } else if(dp.datepicker("option","minDate")){
                    dp.datepicker("setDate", minDate);
                } else {
                    dp.datepicker("setDate", new Date());
                }
            }
            if (dp.datepicker("option","maxDate") && curDate > maxDate) {
                dp.datepicker("setDate", maxDate);
            }
            if (dp.datepicker("option","minDate") && curDate < minDate) {
                dp.datepicker("setDate", minDate);
            }
            dp.data("ftLastGoodDate", dp.datepicker("getDate"));
        });
    });
    // Init help icons
    //console.log('finding help buttons');
    obj.find('a.helpButton,a.helpTopic,.rwdHelpMenu a').not('.ft-helpLink').addClass('ft-helpLink').click(function(e){
        var el = $(this);
        var helpFile = el.attr('data-fthelp');
        $('#rwdNav2>a#rwdNavButton').trigger('ft-close');
        if(helpFile){
            el.foreTeesModal("help",helpFile);
            e.preventDefault();
        } else {
            helpFile = el.attr('data-fthelplink');
            var altTitle = el.attr('data-fthelptitle');
            if(helpFile){
                el.foreTeesModal("help",{fullPath:helpFile,altTitle:altTitle});
                e.preventDefault();
            }         
        }
    });
    ftInitPageHelp(obj);
    // Init Tabs
    var ac = "active"
    var tabs = obj.find('.ft-tabs');
    
    tabs.find('.ft-tab>div').not(".ftInit").addClass("ftInit").click(function(e){
        var el = $(this);
        var parent = el.closest('.ft-tabs');
        //var tab = el.closest('.ft-tab');
        var blockGroup = parent.attr('data-ftTabGroup');
        var blocks = obj.find(blockGroup);
        parent.find('.ft-tab>div').removeClass(ac);
        el.addClass(ac);
        blocks.removeClass(ac);
        parent.parent().find(el.attr('data-ftTab')).addClass(ac);
        ftSizeCheck();
        e.preventDefault();
    });
    tabs.each(function(){
        $(this).find('.ft-tab>div').first().click()
    });
    
    // Activate event buttons
    obj.find(".button_legend_event, .event_button, .menu_list_link>a").not(".ftInit").addClass("ftInit").click(function(e){
        
        $('#rwdNav2>a#rwdNavButton').trigger('ft-close');
        
        e.preventDefault();
        var obj = $(this);
        //console.log(obj.attr("data-ftjson"));
        // Check Json
        var jsondata = obj.attr("data-ftjson");
        try {
            var json = JSON.parse(jsondata);
            //console.log('Parsed Json');
        } catch (e) {
            //console.log('Bad Json');
            return;
        }
        if(typeof json.base_url != "string"){
            json.base_url = '';
        }
        //if(json.type && (json.type == "event" || json.type == "Member_events2")){
        //    json.servlet = "Member_events2";
        //}
        if(typeof json.servlet != "string"){
            json.servlet = "Member_events2";
        }
        
        if(json.type && (json.type == "dining_reservation_list") ){
            obj.foreTeesModal("eventList", json);
        } else {
            obj.foreTeesModal("eventPrompt", json);
        }
        
    });
    // Fix ios issues in premier mode
    var ios = navigator.userAgent.match(/(iPod|iPhone|iPad)/);
    if(ios && parent != window){
        var menuTabs = obj.find('li[aria-haspopup=true]').not('.'+premierfixClass);
        var menuShowClass = 'show_menu';
        var premierfixClass = 'premierFix';

        menuTabs.addClass(premierfixClass).on('touchstart mouseover',function(e){
            var el = $(this);
            menuTabs.removeClass(menuShowClass);
            el.addClass(menuShowClass);
            e.stopPropagation();
        });

        menuTabs.on('mouseout mouseleave',function(e){
            menuTabs.removeClass(menuShowClass);
        });

        $('body').not('.'+premierfixClass).addClass(premierfixClass).on('touchstart',function(){
            menuTabs.removeClass(menuShowClass);
        });
    }
    

    
}

function ftUnHoverMenu(){
    $('#rwdNav .topnav_item ul, #topnav .topnav_item ul').css("display","none").removeClass("show_menu");
    $('#rwdNav .topnav_item').removeClass("show_menu");
    setTimeout(function(){$('#rwdNav .topnav_item ul, #topnav .topnav_item ul').css("display","");},200);
}

//window.addEventListener('message', ftMessageReceiver, false);
$(window).bind('message', function(e){
    ftMessageReceiver(e.originalEvent);
});

function ftUid(pre) {
    if (!pre){
        pre = '';
    }
    var uid = setTimeout(function() {clearTimeout(uid)}, 0);
    return pre + '' + uid;
}


function ftSetMaxIframePad(){
    var maxModalPad = 0;
    //console.log(ftModalPads);
    for (var key in ftModalPads){
        if(maxModalPad < ftModalPads[key]){
            maxModalPad = ftModalPads[key],10;
        }
    }
    //console.log('padding:'+maxModalPad);
    ftModalPad = maxModalPad;
}

var ftRIFT
function ftSizeCheck(){
    if (parent != window  && $.fn.foreTeesSession("get","premier_referrer")) {
        // we are running in a frame so append our resize helper and call it
        
    	
        if(ftRIFT){
            clearTimeout(ftRIFT);
        }
        ftRIFT = setTimeout(function(){ftResizeIframe();},3);
    }
}

// Send command to parent to resize my containing iframe to match my content size
function ftResizeIframe(opt){
    
    if(!(window.location.pathname.match(/.*\/Dining_home/) !== null && (ftGetUriParam('view_reservations') !== null || ftGetUriParam('event_popup') !== null))){
        $('html').css('border','0px none').css('overflow','hidden');
    }
    
    var newHeight = ftGetDocHeight();
    //console.log('using pad:'+ftModalPad);
    //console.log(ftModalPads);
    //console.log("height:"+newHeight);
    if(newHeight > 0){
        //console.log("changing size to:"+(newHeight + iFramePad + ftModalPad));
        //console.log("changing size to:"+(newHeight + iFramePad));
        ftPostMessage(parent,'ftResizeIframeNoCallback',{height:(newHeight)});
        
    }
}

function ftResizeIframeNoCB(){
    
    if(!(window.location.pathname.match(/.*\/Dining_home/) !== null && (ftGetUriParam('view_reservations') !== null || ftGetUriParam('event_popup') !== null))){
        $('html').css('border','0px none').css('overflow','hidden');
    }
    
    //if(!(window.location.pathname.match(/.*\/Dining_home/) !== null && (ftGetUriParam('view_reservations') !== null || ftGetUriParam('event_popup') !== null))){
    //    $('html').css('border','0px none').css('overflow','hidden');
    //}
    
    var newHeight = ftGetDocHeight();
    //console.log('using pad:'+ftModalPad);
    //console.log(ftModalPads);
    //console.log("height:"+newHeight);
    if(newHeight > 0){
        //console.log("changing size to:"+(newHeight + iFramePad + ftModalPad));
        //console.log("changing size to:"+(newHeight + iFramePad));
        ftPostMessage(parent,'ftResizeIframe',{height:(newHeight)});
        
    }
}

function ftGetDocHeight() {
    if (parent != window  && $.fn.foreTeesSession("get","premier_referrer")) {
        var dh = $('body').height();
        // There has to be a better way of doing this, but for now
        // We'll grab the height of absolutly positioned elements and make sure we size for the accordingly
        $('.ui-dialog:visible,.plAddPartner:visible,.ui-accordian:visible').not(':hidden').each(function(){
            var d = $(this);
            var test = Math.round(d.offset().top+d.outerHeight(true));
            if(test>dh){
                dh = test;
            }
        });
        return dh;
    } else {
        return $(document).innerHeight();
    }
}

// Poll my content size, and ask parent to resize me if needed
// We poll rather than bind to DOM modification, etc. for browser compatibility.
// ??? Is this used any longer ???
function ftCheckIframeSize(){
    if(ftDoIframeCheck && ($('body').height()+ftModalPad != ftIframeHeight || $('body').width() != ftIframeWidth)) {
        //console.log('resize:'+ftIframeHeight+' to '+$('body').height());
        ftIframeHeight = $('body').height()+ftModalPad,ftIframeWidth = $('body').width();
        ftResizeIframe();   
    }
}

// Called by some modals to decide what to do on close
function ftIframeAction(opt){
    switch(opt){
        case 'close':
            ft_historyBack();
            break;
    }
}

function ftSlotPost(page,request,exclude,form){
    var extras = [];
    if(request["iframe_action"]){
        extras.push('iframe_action='+request["iframe_action"]);
    }
    if(typeof form == "undefined"){
        form = request;
    }
    if(typeof request.jump == "undefined"){
        //request.jump = "0";
    }
    if(typeof exclude != "object"){
        exclude = {};
    }
    exclude.base_url = true;
    if(!request.base_url){
        request.base_url = '';
    }
    var extra = extras.join('&');
    //console.log(extra);
    var slotContainer = $('.slot_container');
    if(slotContainer.length){
        // We're on a slot page.  Lets try another way
        var slotParms = slotContainer.data("ft-slotParms");
        var cont = false;
        if(slotParms && !slotParms.skip_unload){
            cont = confirm(slotParms.options.navagateAwayMessage);
        } else {
            cont = true;
        }
        if(cont){
            var uri = ftObjectToUri(form,exclude);
            var url = ftSetJsid(request.base_url)+page;
            if(extra.length){
                extra += '&' + uri;
            }
            if(url.indexOf('?') > -1){
                url += '&';
            } else {
                url += '?';
            }
            $('body').foreTeesModal("pleaseWait","open",true);
            slotContainer.foreTeesSlot("leaveForm",false,function(){
                window.location.replace(url+uri); // Leave slot page via replace
            });
        }
    } else {
        if(extra.length){
            extra = '?' + extra;
        }
        var formObj = $('<form action="'+ftSetJsid(request.base_url)+page+extra+'" method="post">' + ftObjectToForm(form,exclude) + '</form>');
        $('body').append(formObj);
        $('body').foreTeesModal("pleaseWait","open",true);
        formObj.submit();
    }
    
    
    return;
}


// Ft slot exit
function ftOnSlotExit(opt){
    
    // Bind to unload, freeing tee time if page is navigated away from
    $(window).unload(opt.onUnload);
    var doOnBefore = true;

    //var prem = $.fn.foreTeesSession("get","premier_referrer");
    if(window != parent){
        // If we are in an iframe, onbeforeunload is pointless 
        doOnBefore = false;
    }

    if(doOnBefore){
        //console.log("adding on before unload");
        window.onbeforeunload = opt.onBeforeUnload;
    }

    // Capture all relevant navigation outside of our slot container, and prompt
    // user about leaving the slot page
    var problemLinks = $('a:not([target]):not([href^="#"]):not([data-fthelp]):not([onclick^="window"])').filter(function(){
        return $(this).parents(".ftS-slot").length < 1
    });
    //problemLinks.addClass("away-from-slot-page-link"); // for debug
    problemLinks.not('.ftSlotConflitInit').addClass('.ftSlotConflitInit').click(function(e){
        //console.log("Problem link detected.");
        var answer = confirm(opt.navagateAwayMessage);
        var el = $(this);
        if(answer){
            opt.onExit();
            window.onbeforeunload = function(){};
            window.location.replace(el.attr('href')); // Keep us from affecting browser history
            e.preventDefault();
        }else{
            $('body').foreTeesModal('pleaseWait','close');
            e.preventDefault();
        }
    });
    
}

function ftTimer(opt){
    
    if(opt.time > -1){
        opt.timer_start = new Date().getTime();

        opt.timer_interval = setInterval(function(){
            var current_time = new Date().getTime();
            var elapsed_time = (current_time - opt.timer_start);
            var time_remaining = (opt.time - elapsed_time);
            if((time_remaining/1000) < (1)){
                time_remaining = 0;
                clearTimeout(opt.timer_interval);
                opt.onTimeout();
            }
            opt.timers.html(ftTimeString(time_remaining));
            if((time_remaining/1000) < opt.lowTimerTrigger){
                opt.timers.addClass("low_timer_trigger");
            }else{
                opt.timers.removeClass("low_timer_trigger");
            }
        },1000);
    }
    return opt.timer_interval;
}

// When we go back, this is what we do
function ft_historyBack(forceUrl){
    //console.log('Going Back:'+ftBackCount);
    ftDoIframeCheck = false;
    if(parent != window  && $.fn.foreTeesSession("get","premier_referrer")){
        // Send message to parent to go back
        //console.log('goback with postMessage');
        $(window).unload();
        ftPostMessage(parent,'ftGoBack',{forceUrl:forceUrl});
    } else {
        // Just go back
        //console.log('goback with history');
        if(!!forceUrl && forceUrl.length){
            window.location.replace(ftSetJsid(forceUrl));
        } else {
            self.history.go(-ftBackCount);
        }
        
    }
}

function ft_logError(errorText, jqXHR){
    
    //console.log(errorText);
    
    var raw_response = "";
    var history_response = "";
    
    /*
    for(var key in ftLastAjaxSettings){
        if(typeof ftLastAjaxSettings[key] == "string"){
            console.log("Last Settings Key:"+key+', Value:'+ftLastAjaxSettings[key]);
        } else {
            console.log("Last Settings Key:"+key+', Type:'+typeof(ftLastAjaxSettings[key]));
        }
    }
    for(var key in ftLastAjaxSettings.contents){
        if(typeof ftLastAjaxSettings.contents[key] == "string"){
            console.log("Contents Key:"+key+', Value:'+ftLastAjaxSettings.contents[key]);
        } else {
            console.log("Contents Key:"+key+', Type:'+typeof(ftLastAjaxSettings.contents[key]));
        }
    }
    */
   /*
   for(var key in jqXHR){
            if(typeof jqXHR[key] == "string"){
                console.log("Last Settings Key:"+key+', Value:'+jqXHR[key]);
            } else {
                console.log("Last Settings Key:"+key+', Type:'+typeof(jqXHR[key]));
            }
        }
    */
   var last_call = ftAjaxCallHistory[ftAjaxCallHistoryLastUuid];
   if(last_call){
       /*
       for(var key in last_call.settings){
            if(typeof last_call.settings[key] == "string"){
                console.log("Last Settings Key:"+key+', Value:'+last_call.settings[key]);
            } else {
                console.log("Last Settings Key:"+key+', Type:'+typeof(last_call.settings[key]));
            }
        }
        */
       if(last_call.start_time){
           raw_response += "Request Start: "+last_call.start_time.format('isoDateTime')+" \n";
       }
       raw_response += "Request End: "+new Date().format('isoDateTime')+" \n";
       if(last_call.settings.url){
           raw_response += "Last Request URL: "+last_call.settings.url +" \n";
       }
       if(last_call.settings.data){
           raw_response += "Last Request DATA: "+last_call.settings.data +" \n";
       }
       
       
    }
    delete ftAjaxCallHistory[ftAjaxCallHistoryLastUuid];
    
    if(jqXHR){
        raw_response += "Status: "+jqXHR.statusText +" \n";
        if(jqXHR.responseText){
            raw_response += "Response:\n" + jqXHR.responseText;
        } else if(jqXHR.responseXML) {
            raw_response += "Response:\n" + jqXHR.responseXML;
        }
    }
   
   history_response += "\n===================================================== \n"
   history_response += "Ajax history from oldest to newest:"
   
   for (var key in ftAjaxCallHistory){
       last_call = ftAjaxCallHistory[key];
       history_response += "\n===================================================== \n"
        if(last_call.start_time){
           history_response += "Request Start: "+last_call.start_time.format('isoDateTime')+" \n";
       }
       if(last_call.end_time){
           history_response += "Request End: "+last_call.end_time.format('isoDateTime')+" \n";
       }
       if(last_call.settings.url){
           history_response += "Last Request URL: "+last_call.settings.url +" \n";
       }
        if(last_call.settings.data){
           history_response += "Last Request DATA: "+last_call.settings.data +" \n";
       }
       if(last_call.xhr){
           history_response += "Status: "+last_call.xhr.statusText +" \n";
            if(last_call.xhr.responseText){
                history_response += "Response:\n" + last_call.xhr.responseText;
            } else if(last_call.xhr.responseXML) {
                history_response += "Response:\n" + last_call.xhr.responseXML;
            }
        }
       
   }
   ftAjaxCallHistory = {};
   
    var postdata = {
        appCodeName: navigator.appCodeName,
        appName: navigator.appName,
        cookieEnabled: navigator.cookieEnabled,
        platform: navigator.platform,
        userAgent: navigator.userAgent,
        error: errorText,
        rawResponse: raw_response + history_response,
        page: document.URL,
        userName: $.fn.foreTeesSession("get","user"),
        clubName: $.fn.foreTeesSession("get","club")
    }
    $.ajax({  
        url: "data_logger",
        dataType: 'text',
        success: function(data){

        },
        type:'POST',
        data:postdata,
        error: function(){

        }
    });

}

// Get a URI parameter
function getParameterByName(name)
{
    name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
    var regexS = "[\\?&]" + name + "=([^&#]*)";
    var regex = new RegExp(regexS);
    var results = regex.exec(window.location.href);
    if(results == null)
        return "";
    else
        return decodeURIComponent(results[1].replace(/\+/g, " "));
}

// Convert key/string pair in an object to a string of hidden form elements.
function ftObjectToForm(obj, exclude){
    
    var html = "";
    if(typeof exclude === "undefined" || exclude == null){
        exclude = {};
    }
    for (var key in obj){
        var item = obj[key];
        if(typeof item != "object" && typeof exclude[key] == "undefined"){
            //console.log("putting:"+key+"="+item);
            html += "<input type=\"hidden\" name=\"" + key + "\" value=\"" + item + "\">";
        }
    }
    //// console.log("data:"+html);
    return html;
    
}

// Convert key/string pair in an object to a URI string .
function ftObjectToUri(obj, exclude){
    
    var uri = "";
    if(exclude === undefined || exclude == null){
        exclude = {};
    }
    for (var key in obj){
        var item = obj[key];
        //console.log("searching:"+key);
        if(typeof item != "object" && typeof exclude[key] == "undefined"){
            if(uri.length){
                uri += "&";
            }
            uri += encodeURIComponent(key) + "=" + encodeURIComponent(item);
        }
    }
    //// console.log("data:"+uri);
    return uri;
    
}

function ftUriToObject(uri){
    var uri_a = uri.split("&");
    var obj = {};
    for (var i = 0; i < uri_a.length; i++){
        var uric_a = uri_a[i].split("=");
        obj[decodeURIComponent(uric_a[0])] = ((uric_a[1] !== undefined)?decodeURIComponent(uric_a[1]):"");
    }
    return obj;
}

// Replace parameters in a list with the value of the object
function ftReplaceKeyInString(str, obj){
    if(typeof str == "string"){
        var parms = str.match(/\[[^\]]+\]/g);
        if(parms != null){
            for(var i = 0; i < parms.length; i++){
                //console.log("parseing:"+parms[i]);
                var parm = parms[i].replace(/(^\[)|(\]$)/g,"");
                var keys = parm.split(/\./g);
                var replacement = obj;
                for(var i2 = 0; i2 < keys.length; i2++){
                    if(typeof replacement != "undefined"){
                        if(Object.prototype.toString.apply(replacement) === '[object Array]'){
                            replacement = replacement[parseInt(keys[i2], 10)];
                        }else{
                            replacement = replacement[keys[i2]];
                        }
                        if(typeof replacement == "undefined"){
                            break;
                        }
                    }else{
                        break;
                    }
                }
                if(typeof replacement != "undefined" && typeof replacement != "object" && typeof replacement != "function"){
                    //console.log("found:"+replacement);
                    if(typeof replacement == "string" ){
                        replacement = ftReplaceKeyInString(replacement, obj); // set any parameters in the replacement
                    }
                    str = str.replace(parms[i], replacement);
                } else if (typeof replacement == "object") {
                    return replacement;
                }else{
            //str = str.replace(parm, parm+" not found");
            }
            }
        }
    } else if (typeof str == "function"){
        return "";
    }
    return str;
}

// Location change
function ftChangePage(url){
    if($.mobile){
        $.mobile.changePage(url);
    } else {
        window.location.href = url;
    }
}

// Find a partial key in an object using key*, if string, or using regexp if passed.
// Return first matched value
function ftFindKey(exp, obj){
    if(!(exp instanceof RegExp)){
        exp = new RegExp("^"+exp,"");
    }
    for (var key in obj){
        if(key.match(exp)){
            return obj[key];
        }
    }
    return null;
    
}

// Return a new date object from the integer date format YYYYMMDD and optional integer time hhmm
function ftIntDateToDate(intDate, intTime){
    var strDate = intDate.toString();
    var year = parseInt(strDate.substr(0,4), 10);
    var month = parseInt(strDate.substr(4,2), 10) - 1;
    var day = parseInt(strDate.substr(6,2), 10);
    var hour = 0;
    var min = 0;
    if(typeof intTime != "undefined"){
        var strTime = intTime.toString();
        min = parseInt(strTime.substr(2,2), 10);
        hour = parseInt(strTime.substr(0,((strTime.length == 3)?1:2)), 10);
    }
    return new Date(year, month, day, hour, min, 0, 0);
};

// Return a new date object from the string date format MM/DD/YYYY
function ftStringDateToDate(strDate){
    var date_array = strDate.split("/");
    var year = parseInt(date_array[2], 10);
    var month = parseInt(date_array[0], 10) - 1;
    var day = parseInt(date_array[1], 10);
    return new Date(year, month, day, 0, 0, 0, 0);
}

function ftMDYToInt(strDate){
    
    var date_array = strDate.split("/");
    var year = parseInt(date_array[2], 10);
    var month = parseInt(date_array[0], 10);
    var day = parseInt(date_array[1], 10);
    return (year * 10000) + (month * 100) + day;

}

function ftYMDToInt(strDate){
    var date_array = strDate.split("/");
    var year = parseInt(date_array[0], 10);
    var month = parseInt(date_array[0], 10);
    var day = parseInt(date_array[2], 10);
    return (year * 10000) + (month * 100) + day;
}

// Extract value using regexp, return result
function ftExtractValue(data, regexp, nullvalue){
    var match = data.match(regexp);
    var result = nullvalue;
    if(match != null && typeof match[1] == "string"){
        result = match[1];
    }else if(match != null && typeof match[0] == "string"){
        result = match[0];
    }
    return result;
}

function ftPad(num, size) {
    var s = "000000000" + num;
    return s.substr(s.length-size);
}

function ftIsMobile(check){
    var chk = window.ftUserAgentCheck;
    if(!chk){
        var ua = navigator.userAgent;
        chk = {
            iphone: ua.match(/(iPhone|iPod)/),
            ios5: ua.match(/(iPhone|iPod|iPad).*OS 5_\d/),
            ipad: ua.match(/(iPad)/),
            ios: ua.match(/(iPhone|iPod|iPad)/),
            blackberry: ua.match(/BlackBerry/),
            android: ua.match(/Android/),
            iemobile: ua.match(/IEMobile/),
            dolphin: ua.match(/Dolfin/),
            opera: ua.match(/Opera Mobi/),
            skyfire: ua.match(/Skyfire/),
            webkit: ua.match(/AppleWebkit/),
            ie9mobile: ua.match(/IEMobile\/9\./),
            ie10: ua.match(/MSIE 10/),
            ie11: ua.match(/Trident.*rv.11\./)
        };
        window.ftUserAgentCheck = chk; // Cache it for later use
    }
    if(typeof check == "string" && (check in chk) && chk[check]){
        return true;
    }else if(typeof check != "string" && (
        chk.ios || chk.android || chk.blackberry || 
        chk.iemobile || chk.iemobile || chk.dolphin ||
        chk.opera || chk.skyfire )){
        return true;
    }else{
        return false;
    }
}

function ftCount(obj){
    var count = 0;
    for(var i in obj){
        count ++;
    }
    return count;
}

function ftTimeString(time){
    
    var hours = Math.floor(time / (1000*60*60));
    var min = Math.floor((time % (1000*60*60)) / (1000*60));
    var sec = Math.floor(((time % (1000*60*60)) % (1000*60)) / 1000);
    
    if (hours < 10 && hours > 0) hours = "0" + hours;
    if (min < 10) min = "0" + min;
    if (sec < 10) sec = "0" + sec;
    var timeString = "";
    if(hours>0){
        timeString += hours + ":";
    }
    timeString += min + ":" + sec;
    return timeString;
    
}


function ftEscapeRegExp(str) {
    return str.replace(/[-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
}

function ftRemoveExtra(str) {
    str = str.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
    return str.replace(/[^[a-zA-Z0-9 ]/ig, ""); 
}

// Pull a parameter frmo the URI of url
function ftGetUriParam( name, url ) {
    if(typeof url != "string"){
        url = window.location.href;
    }
    var regexS = "[\\?&]"+ftEscapeRegExp(name)+"={0,1}([^&#]*)";
    var regex = new RegExp( regexS );
    var results = regex.exec( url );
    if( results == null )
            return null;
    else
            return decodeURIComponent(results[1]);
}

function ftUpdateQueryString(key, value, url, no_null) {
    if(!no_null || typeof url == "string"){
        if (typeof url != "string") url = window.location.href;
        var re = new RegExp("([?|&])" + key + "=.*?(&|#|$)(.*)", "gi");
        var rt = new RegExp("([?|&])" + key + "([#?&]|$)(.*)", "gi");

        if (re.test(url)) {
            if (typeof value !== 'undefined' && value !== null) {
                //console.log("replace");
                url = url.replace(re, '$1' + encodeURIComponent(key) + "=" + encodeURIComponent(value) + '$2$3');
            } else {
                //console.log("remove");
                url = url.replace(re, '$1$3').replace(/(&|\?)$/, '');
            }
        } else if (rt.test(url)){
            if (typeof value !== 'undefined' && value !== null){
                //console.log("replaceNN");
                url = url.replace(rt, '$1' + encodeURIComponent(key) + "=" + encodeURIComponent(value) + '$2$3');
            } else {
                //console.log("removeNN");
                url = url.replace(rt, '$1$2$3').replace(/(&|\?)$/, '');
            }
        }
        else {
            if (typeof value !== 'undefined' && value !== null) {
                //console.log("add");
                var separator = url.indexOf('?') !== -1 ? '&' : '?',
                    hash = url.split('#');
                url = hash[0] + separator + encodeURIComponent(key) + '=' + encodeURIComponent(value);
                if (hash[1]) url += '#' + hash[1];
            }
        }
    }
    return url;
}

// Change file/servlet of current url
function ftChangeFile( newfile, url ) {
    if(typeof url != "string"){
        url = window.location.href;
    }
    var new_url = url;
    //console.log("URL:"+url);
    var regex = new RegExp( /\/([^\/\?#]+[\?#]*[^\/]*$)/ );
    var results = regex.exec( url );
    //console.log("RXP:"+results);
    if(typeof results[1] != "undefined"){
        regex = new RegExp( "[/]"+ftEscapeRegExp(results[1])+"[^/]*" );
        new_url = url.replace(regex, '/'+newfile);
    }
    return new_url;
}

function ftFindAllInString(array, string){
    var match = true;
    var searchTarget = ftRemoveExtra(string);
    for (var i in array){
        var keyword = ftRemoveExtra(array[i]);
        //console.log("searching["+i+"] for:"+keyword);
        if(!searchTarget.match(new RegExp("(^|\\s|,)"+ftEscapeRegExp(keyword),"i"))){
            match = false;
        }
        
    }
    return match;
}

function ftFillParemetersInObject(object, source){
    var result = {};
    for(var parm in object){
        switch(typeof(object[parm])){
            case "string":
                result[parm] = ftReplaceKeyInString(object[parm],source);
                break;
        }
    }
    return result; 
}

function ftMixedObjectToObject(object){
    var result = {};
    for(var parm in object){
        switch(typeof(object[parm])){
            case "undefined":
            case "function":
                //do nothing
                break;
            case "object":
                if($.isArray(object[parm])){
                    $.extend(result,ftObjectFromArray(parm, object[parm]));
                }
                break;
            default:
                result[parm] = object[parm];
                break;
        }
    }
    return result;
}

function ftFieldToObject(field, name){
    var result = {};
    switch(typeof(field)){
        case "undefined":
        case "function":
            //do nothing
            break;
        case "object":
            //if($.isArray(field)){
                $.extend(result,ftObjectFromArray(name, field));
            //}
            break;
        default:
            result[name] = field;
            break;
    }
    return result;
}

function ftObjectFromArray(name, array){
    var object = {};
    for(var i in array){
        var val = array[i];
        var field;
        if(typeof val == "object"){
            for(var v in val){
                field = name.replace(/\%/i,""+(parseInt(i, 10)+1)+"_"+v).replace(/\#/i,i+"_"+v);
                object[field] = val[v];
            }
        } else {
            field = name.replace(/\%/i,""+(parseInt(i, 10)+1)).replace(/\#/i,""+i);
            object[field] = val;
        }
    }
    return object;
}

function ftSetIfDefined(array, index, value, deflt){
    if(typeof deflt == "undefined" || deflt == null){
        deflt = "";
    }
    if((typeof array != "undefined") && (typeof array[index] != "undefined")){
        if(typeof value == "undefined" || value == null){
            array[index] = deflt;   
        }else{
            array[index] = value;
        }
    }
    return;
}

// Case insensitive key search
function ftGetKeyIn(key, obj){
    key = key.toLowerCase();
    for (var k in obj){
        if(k.toLowerCase() == key){
            return obj[k];
        }
    }
    return null;
}

function ftPadInt(num, size){
    var neg = "";
    if(num < 0){
        neg = "-";
        num = Math.abs(num);
    }
    var s = num+"";
    while (s.length < size) s = "0" + s;
    return neg+s;
}

// Add timestamp to URL
function ftStampUrl(url){
    var uria = url.split("?");
    var uri = '';
    var tstamp = new Date().getTime();
    if(uria[1]){
        uri = uria[1] + "&_=" + tstamp;
    } else {
        uri = "_=" + tstamp;
    }
    return uria[0] + "?" + uri;
}

function ftLocation(url){
    window.location = ftSetJsid(ftStampUrl(url));
}

function ftFixCalendarCells(){
    //console.log('fixcal');
    /*
    var ignoreClick = function(e){
        e.stopPropagation();
    }
    */
    //var cal = $('.list_calendar');
    ftcalendar.activate($('table.list_calendar').not('.ftcal_dynamic'));
}

function ftScrollWindow(pos) {
    if(parent != window  && $.fn.foreTeesSession("get","premier_referrer")){
        // Premier mode.  Scroll parent window
        // (Need to get premier pointing to dev2 to finish this)
    } else {
        // Scroll this window
        $(window).scrollTop(pos);
    }
}


// Control Sorting and deleting in Member_partner
function ftPartnerSort(obj){
    //$( "ul.partnerList" ).sortable( {
    //console.log("partner sort");
    if(!obj.length || obj.data("ftPartnerSort")){
        return;
    }
    obj.data("ftPartnerSort",true);
    $(obj).sortable( {
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
    
    obj.find( "a.deleteButtonSmall").click( function(){
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

}

function ftEscape(str) {
    return String(str)
            .replace(/&/g, '&amp;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');
}

function ftGetTagNames(){
    var rwd = $.fn.foreTeesSession('get','rwd');
    var div = 'div';
    function _sc(sclass){
        if(sclass){
            return sclass
        } else {
            return '';
        }
    }
    var tags = {
        table:rwd?div:'table',
        caption:rwd?div:'caption',
        tbody:rwd?div:'tbody',
        thead:rwd?div:'thead',
        tr:rwd?div:'tr',
        td:rwd?div:'td',
        th:rwd?div:'th',
        t:{
            sbi:'sub_instructions ',
            mni:'main_instructions ',
            cap:'rwdCaption ',
            tbl:'rwdTable ',
            tbd:'rwdTbody ',
            the:'rwdThead ',
            tr:'rwdTr ',
            th:'rwdTh ',
            td:'rwdTd '
        },
        fn:{
            
            oSubInst:function(sclass,attr){
                return tags.fn.oTag(div,tags.t.sbi+_sc(sclass),attr);
            },
            cSubInst:function(){
                return tags.fn.cTag(div);
            },
            subInst:function(content,sclass,attr){
                return tags.fn.tag(div,content,tags.t.sbi+_sc(sclass),attr);
            },
            oMainInst:function(sclass,attr){
                return tags.fn.oTag(div,tags.t.mni+_sc(sclass),attr);
            },
            cMainInst:function(){
                return tags.fn.cTag(div);
            },
            mainInst:function(content,sclass,attr){
                return tags.fn.tag(div,content,tags.t.mni+_sc(sclass),attr);
            },
            oCaption:function(sclass,attr){
                return tags.fn.oTag(tags.caption,tags.t.cap+_sc(sclass),attr);
            },
            oTable:function(sclass,attr){
                return tags.fn.oTag(tags.table,tags.t.tbl+_sc(sclass),attr);
            },
            oTbody:function(sclass,attr){
                return tags.fn.oTag(tags.tbody,tags.t.tbd+_sc(sclass),attr);
            },
            oThead:function(sclass,attr){
                return tags.fn.oTag(tags.thead,tags.t.the+_sc(sclass),attr);
            },
            oTr:function(sclass,attr){
                return tags.fn.oTag(tags.tr,tags.t.tr+_sc(sclass),attr);
            },
            oTh:function(sclass,attr){
                return tags.fn.oTag(tags.th,tags.t.th+_sc(sclass),attr);
            },
            oTd:function(sclass,attr){
                return tags.fn.oTag(tags.td,tags.t.td+_sc(sclass),attr);
            },
            cCaption:function(){
                return tags.fn.cTag(tags.caption);
            },
            cTable:function(){
                return tags.fn.cTag(tags.table);
            },
            cTbody:function(){
                return tags.fn.cTag(tags.tbody);
            },
            cThead:function(){
                return tags.fn.cTag(tags.thead);
            },
            cTr:function(){
                return tags.fn.cTag(tags.tr);
            },
            cTh:function(){
                return tags.fn.cTag(tags.th);
            },
            cTd:function(){
                return tags.fn.cTag(tags.td);
            },
            caption:function(content,sclass,attr){
                return tags.fn.tag(tags.caption,content,tags.t.cap+_sc(sclass),attr);
            },
            table:function(content,sclass,attr){
                return tags.fn.tag(tags.table,content,tags.t.tbl+_sc(sclass),attr);
            },
            tbody:function(content,sclass,attr){
                return tags.fn.tag(tags.tbody,content,tags.t.tbd+_sc(sclass),attr);
            },
            thead:function(content,sclass,attr){
                return tags.fn.tag(tags.thead,content,tags.t.the+_sc(sclass),attr);
            },
            tr:function(content,sclass,attr){
                return tags.fn.tag(tags.tr,content,tags.t.tr+_sc(sclass),attr);
            },
            th:function(content,sclass,attr){
                return tags.fn.tag(tags.th,content,tags.t.th+_sc(sclass),attr);
            },
            td:function(content,sclass,attr){
                return tags.fn.tag(tags.td,content,tags.t.td+_sc(sclass),attr);
            },
            tabs:function(tabGroup,tabs,tabsClass){
                if(!tabsClass){
                    tabsClass = '';
                }
                var res = tags.fn.oTag('ul','ft-tabs '+tabsClass,{'data-ftTabGroup':'.'+tabGroup});
                if(typeof tabs == "object"){
                    for(var i in tabs){
                        res += tags.fn.tag('li',tags.fn.tag('div',tabs[i],'',{'data-ftTab':'.'+i}),'ft-tab');
                    }
                }
                res += tags.fn.cTag('ul');
                return res;
            },
            tabBlocks:function(tabGroup,blocks,blockClass){
                if(!blockClass){
                    blockClass = '';
                }
                var res = '';
                if(typeof blocks == "object"){
                    for(var i in blocks){
                        res += tags.fn.tag('div',blocks[i],'ft-tabBlock '+tabGroup+' '+i);
                    }
                }
                return res;
            },
            oTag:function(tag,sclass,attr){
                return tags.fn.tag(tag,null,sclass,attr,true);
            },
            cTag:function(tag){
                return tags.fn.tag(tag,null,null,null,null,true);
            },
            tag:function(tag,content,sclass,attr,open_only,close_only){
                var res = '';
                if(!close_only){
                    res += '<'+tag;
                    if(sclass){
                        res += ' class="'+sclass.trim()+'"';
                    }
                    if(typeof attr == "object"){
                        for(var i in attr){
                            res += ' '+i;
                            if(attr[i].length){
                                res += '="'+ftEscape(attr[i])+'"';
                            }
                        }
                    }
                    res += '>';
                    if(typeof content != 'undefined' && content != null){
                        res += content;
                    }
                }
                if(!open_only){
                    res += '</'+tag+'>';
                }
                return res;
            }
            
        }
    }
    return tags;
}



/*************************************************************
*
* Misc jQuery plugins
*  
*************************************************************/

// Highlight an item by pulsing the background color for a short time
jQuery.fn.foreTeesHighlight = function(color, duration) {
    var o = $(this);
    o.stop(true,true);
    var oBgColor = o.css("background-color");
    o.data("ft-highlightOBgColor", oBgColor);
    o.css("background-color", color).animate(
    {
        backgroundColor:oBgColor
    },
    {
        duration: duration,
        complete: function(){
            o.css("background-color", oBgColor);
        },
        step: function(){
            o.children().addClass("refresh-element").removeClass("refresh-element");
        }
    });
};

jQuery.fn.formToObject = function() {
    
    var namevalue = $(this).serializeArray();
    var result = {};
    for(var i = 0; i < namevalue.length; i++){
        result[namevalue[i].name] = namevalue[i].value;
    }
    return result;
    
};

(function($) {
    $.ftQueryString = (function(a) {
        if (a == "") return {};
        var b = {};
        for (var i = 0; i < a.length; ++i)
        {
            var p=a[i].split('=');
            if (p.length != 2) continue;
            b[p[0]] = decodeURIComponent(p[1].replace(/\+/g, " "));
        }
        return b;
    })(window.location.search.substr(1).split('&'))
})(jQuery);

/* local storage emulation for browsers that don't support it */
if (!window.localStorage) {
    Object.defineProperty(window, "localStorage", new (function () {
        var aKeys = [], oStorage = {};
        Object.defineProperty(oStorage, "getItem", {
            value: function (sKey) {
                return sKey ? this[sKey] : null;
            },
            writable: false,
            configurable: false,
            enumerable: false
        });
        Object.defineProperty(oStorage, "key", {
            value: function (nKeyId) {
                return aKeys[nKeyId];
            },
            writable: false,
            configurable: false,
            enumerable: false
        });
        Object.defineProperty(oStorage, "setItem", {
            value: function (sKey, sValue) {
                if(!sKey) {
                    return;
                }
                document.cookie = escape(sKey) + "=" + escape(sValue) + "; expires=Tue, 19 Jan 2038 03:14:07 GMT; path=/";
            },
            writable: false,
            configurable: false,
            enumerable: false
        });
        Object.defineProperty(oStorage, "length", {
            get: function () {
                return aKeys.length;
            },
            configurable: false,
            enumerable: false
        });
        Object.defineProperty(oStorage, "removeItem", {
            value: function (sKey) {
                if(!sKey) {
                    return;
                }
                document.cookie = escape(sKey) + "=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/";
            },
            writable: false,
            configurable: false,
            enumerable: false
        });
        this.get = function () {
            var iThisIndx;
            for (var sKey in oStorage) {
                iThisIndx = aKeys.indexOf(sKey);
                if (iThisIndx === -1) {
                    oStorage.setItem(sKey, oStorage[sKey]);
                }
                else {
                    aKeys.splice(iThisIndx, 1);
                }
                delete oStorage[sKey];
            }
            for (aKeys; aKeys.length > 0; aKeys.splice(0, 1)) {
                oStorage.removeItem(aKeys[0]);
            }
            for (var aCouple, iKey, nIdx = 0, aCouples = document.cookie.split(/\s*;\s*/); nIdx < aCouples.length; nIdx++) {
                aCouple = aCouples[nIdx].split(/\s*=\s*/);
                if (aCouple.length > 1) {
                    oStorage[iKey = unescape(aCouple[0])] = unescape(aCouple[1]);
                    aKeys.push(iKey);
                }
            }
            return oStorage;
        };
        this.configurable = false;
        this.enumerable = true;
    })());
}




/** (No longer needed.  Fixed in jQuery UI 1.10+)
* This is part of a patch to address a jQueryUI bug.  The bug is responsible
* for the inability to scroll a page when a modal dialog is active. If the content
* of the dialog extends beyond the bottom of the viewport, the user is only able
* to scroll with a mousewheel or up/down keyboard keys.
*
* @see http://bugs.jqueryui.com/ticket/4671
* @see https://bugs.webkit.org/show_bug.cgi?id=19033
* @see /views_ui.module
* @see /js/jquery.ui.dialog.min.js
*
* This javascript patch overwrites the $.ui.dialog.overlay.events object to remove
* the mousedown, mouseup and click events from the list of events that are bound
* in $.ui.dialog.overlay.create
*
* The original code for this object:
* $.ui.dialog.overlay.events: $.map('focus,mousedown,mouseup,keydown,keypress,click'.split(','),
*  function(event) { return event + '.dialog-overlay'; }).join(' '),
*
*/
/*
(function( $, undefined ) {
    if ($.ui && $.ui.dialog && $.ui.version == "1.8.16") {
        $.ui.dialog.overlay.events = $.map('focus,keydown,keypress'.split(','), function(event) {
            return event + '.dialog-overlay';
        }).join(' ');
    }
}(jQuery));
*/

/*
Copyright 2014 Mike Dunn
http://upshots.org/
Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:
The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
(function($){
	
	$.fn.getStyles = function(only, except) {
		
		// the map to return with requested styles and values as KVP
		var product = {};
		
		// the style object from the DOM element we need to iterate through
		var style;
		
		// recycle the name of the style attribute
		var name;
		
		// if it's a limited list, no need to run through the entire style object
		if (only && only instanceof Array) {
			
			for (var i = 0, l = only.length; i < l; i++) {
				// since we have the name already, just return via built-in .css method
				name = only[i];
				product[name] = this.css(name);
			}
			
		} else {
		
			// prevent from empty selector
			if (this.length) {
				
				// otherwise, we need to get everything
				var dom = this.get(0);
				
				// standards
				if (window.getComputedStyle) {
					
					// convenience methods to turn css case ('background-image') to camel ('backgroundImage')
					var pattern = /\-([a-z])/g;
					var uc = function (a, b) {
							return b.toUpperCase();
					};			
					var camelize = function(string){
						return string.replace(pattern, uc);
					};
					
					// make sure we're getting a good reference
					if (style = window.getComputedStyle(dom, null)) {
						var camel, value;
						// opera doesn't give back style.length - use truthy since a 0 length may as well be skipped anyways
						if (style.length) {
							for (var i = 0, l = style.length; i < l; i++) {
								name = style[i];
								camel = camelize(name);
								value = style.getPropertyValue(name);
								product[camel] = value;
							}
						} else {
							// opera
							for (name in style) {
								camel = camelize(name);
								value = style.getPropertyValue(name) || style[name];
								product[camel] = value;
							}
						}
					}
				}
				// IE - first try currentStyle, then normal style object - don't bother with runtimeStyle
				else if (style = dom.currentStyle) {
					for (name in style) {
						product[name] = style[name];
					}
				}
				else if (style = dom.style) {
					for (name in style) {
						if (typeof style[name] != 'function') {
							product[name] = style[name];
						}
					}
				}
			}
		}
		
		// remove any styles specified...
		// be careful on blacklist - sometimes vendor-specific values aren't obvious but will be visible...  e.g., excepting 'color' will still let '-webkit-text-fill-color' through, which will in fact color the text
		if (except && except instanceof Array) {
			for (var i = 0, l = except.length; i < l; i++) {
				name = except[i];
				delete product[name];
			}
		}
		
		// one way out so we can process blacklist in one spot
		return product;
	
	};
	
	// sugar - source is the selector, dom element or jQuery instance to copy from - only and except are optional
	$.fn.copyCSS = function(source, only, except) {
		var styles = $(source).getStyles(only, except);
		this.css(styles);
		
		return this;
	};
	
})(jQuery);
