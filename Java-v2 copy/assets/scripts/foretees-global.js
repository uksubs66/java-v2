// Default settings (use club javascript files to override)

$.fn.foreTeesMemberCalendar("setOptions", {
    toolTips: {
        tlt:'Availible',
        0:'View Only',
        1:'Tee Times Available',
        2:'Lottery'
    }
});

if ( $.mobile ) {
    // If using jquery mobile
    $.fn.ftDialog = $.fn.popup;
} else {
    //console.log($.fn.dialog);
    $.fn.ftDialog = $.fn.dialog;
}

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
        var options = {
            showOn: "both",
            changeMonth: true,
            changeYear: true,
            format: 'mm/dd/yyyy',
            buttonImage: "../assets/images/calendar_field.png",
            onSelect: function(){
                $(this).change();
            }
        };
        $(this).mask("99/99/9999");
        if($(this).attr("data-ftstartdate")){
            options.minDate = ftStringDateToDate($(this).attr("data-ftstartdate"));
        }
        if($(this).attr("data-ftenddate")){
            options.maxDate = ftStringDateToDate($(this).attr("data-ftenddate"));
        }
        $(this).datepicker(options);
        $(this).change(function(){
            var curDate = $(this).datepicker("getDate");
            var maxDate = new Date($(this).datepicker("option","maxDate"));
            var minDate = new Date($(this).datepicker("option","minDate"));
            if(isNaN(Date.parse($(this).val()))){
                if($(this).data("ftLastGoodDate")){
                    $(this).datepicker("setDate", $(this).data("ftLastGoodDate"));
                } else if($(this).datepicker("option","maxDate")){
                    $(this).datepicker("setDate", maxDate);
                } else if($(this).datepicker("option","minDate")){
                    $(this).datepicker("setDate", minDate);
                } else {
                    $(this).datepicker("setDate", new Date());
                }
            }
            if ($(this).datepicker("option","maxDate") && curDate > maxDate) {
                $(this).datepicker("setDate", maxDate);
            }
            if ($(this).datepicker("option","minDate") && curDate < minDate) {
                $(this).datepicker("setDate", minDate);
            }
            $(this).data("ftLastGoodDate", $(this).datepicker("getDate"));
        });
    });
}

//window.addEventListener('message', ftMessageReceiver, false);
$(window).bind('message', function(e){
    ftMessageReceiver(e.originalEvent);
});

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

// Send command to parent to resize my containing iframe to match my content size
function ftResizeIframe(opt){
    if(!(window.location.pathname.match(/.*\/Dining_home/) !== null && (ftGetUriParam('view_reservations') !== null || ftGetUriParam('event_popup') !== null))){
        $('html').css('border','0px none').css('overflow','hidden');
    }
    var newHeight = $('body').height();
    //console.log('using pad:'+ftModalPad);
    //console.log(ftModalPads);
    if(newHeight > 0){
        ftPostMessage(parent,'ftResizeIframe',{height:newHeight + iFramePad + ftModalPad});
        
    }
}

// Poll my content size, and ask parent to resize me if needed
// We poll rather than bind to DOM modification, etc. for browser compatibility.
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
    var extra = extras.join('&');
    //console.log(extra);
    if(extra.length){
        extra = '?' + extra;
    }
    var formObj = $('<form action="'+ftSetJsid(request['base_url'])+page+extra+'" method="post">' + ftObjectToForm(form,exclude) + '</form>');
    $('body').append(formObj);
    formObj.submit();
    return;
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
            window.location = ftSetJsid(forceUrl);
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
    if(typeof str != "undefined"){
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

function ftIsMobile(check){
    var ft_ua = navigator.userAgent;
    var ft_checker = {
        iphone: ft_ua.match(/(iPhone|iPod)/),
        ios5: ft_ua.match(/(iPhone|iPod|iPad).*OS 5_\d/),
        ipad: ft_ua.match(/(iPad)/),
        ios: ft_ua.match(/(iPhone|iPod|iPad)/),
        blackberry: ft_ua.match(/BlackBerry/),
        android: ft_ua.match(/Android/),
        ie11: ft_ua.match(/Trident.*rv.11\./)
    };
    if(typeof check == "string" && (check in ft_checker) && ft_checker[check]){
        return true;
    }else if(typeof check != "string" && (ft_checker.ios || ft_checker.android || ft_checker.blackberry)){
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

        if (re.test(url)) {
            if (typeof value !== 'undefined' && value !== null)
                url = url.replace(re, '$1' + encodeURIComponent(key) + "=" + encodeURIComponent(value) + '$2$3');
            else {
                url = url.replace(re, '$1$3').replace(/(&|\?)$/, '');
            }
        }
        else {
            if (typeof value !== 'undefined' && value !== null) {
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
            if($.isArray(field)){
                $.extend(result,ftObjectFromArray(name, field));
            }
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
        var field = name.replace(/\%/i,""+(parseInt(i, 10)+1));
        object[field] = array[i];
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

function ftGetReservedDiningUsers(){
    var users = [];
    $('td>input[name="duid"]').each(function(){
        var row = $(this).closest('tr');
        var new_name = $.trim(row.find('td input[name^="name_"]').val());
        var orig_name = $.trim(row.find('td input[name="orig_name"]').val());
        var duid = parseInt($(this).val(),10);
        var rel_id = parseInt(row.find('td input[name^="reservation_id_for_person_"]').val(),10);
        if(orig_name.length && duid && rel_id){
            users.push({
                duid:duid,
                name:orig_name,
                rel_id:rel_id
            });
        }
        
    });
    return users;
}

function ftGetReservationRelations(){
    var relations = {};
    $('td>input[name="duid"]').each(function(){
        var row = $(this).closest('tr');
        var new_name = $.trim(row.find('td input[name^="name_"]').val());
        var orig_name = $.trim(row.find('td input[name="orig_name"]').val());
        var row_name = $.trim(row.find('td input[name^="data_"]').attr('name'));
        var row_number = parseInt(row_name.split('_')[1],10);
        var dining_id = parseInt(row.find('td input[name^="data_"]').val(),10);
        var duid = parseInt($(this).val(),10);
        var rel_id = parseInt(row.find('td input[name^="reservation_id_for_person_"]').val(),10);
        relations['rel_'+rel_id] = {
            duid:duid,
            orig_name:orig_name,
            new_name:new_name,
            row_number:row_number,
            dining_id:dining_id,
            rel_id:rel_id
        };
    });
    return relations;
}

function ftRemoveReservationRelationSlot(slot){
    var row = $('td>input[name="data_'+slot+'"]').closest('tr');
    row.find('td input[name^="name_"]').val('');
    row.find('td input[name="orig_name"]').val('');
    row.find('td input[name="duid"]').val('0');
    row.find('td input.res_name').val('');
    row.find('td input[name="related_reservations['+slot+'][user_identity]"]').val('');
    row.find('td input[name^="reservation_id_for_person_"]').val('0');
}

function ftRemoveReservationPrompt(data, srcObj){
    var relations = ftGetReservationRelations();
    //console.log(relations);
    var thisRel = false;
    var event_id = $('input[name="event_id"]').val();
    if(!event_id){
        event_id = 0;
    }
    event_id = parseInt(event_id,10);
    if(event_id){
        data['event_id'] = event_id;
    }
    if(!relations['rel_'+parseInt(data['reservation_id'],10)]){
        //console.log('Row has already been cleared');
        data['reservation_id'] = '0';
        thisRel = relations['rel_0'];
    } else {
        thisRel = relations['rel_'+parseInt(data['reservation_id'],10)];
    }
    //console.log(thisRel);
    var row = $(srcObj).closest('tr');
    var row_name = $.trim(row.find('td input[name^="data_"]').attr('name'));
    var row_number = parseInt(row_name.split('_')[1],10);
    if(!parseInt(data['reservation_id'],10) && row_number > 1){
        // Unregistered user (on form, but not submitted to dining)
        ftRemoveReservationRelationSlot(row_number);
        
    } else if(!parseInt(data['reservation_id'],10) && row_number == 1){
        
        // Trying to remove self from new registration
        $(document).foreTeesModal("alertNotice", {
            width:700,
            title:"Unable to remove",
            message_head: 'You must be part of any reservation you create.',
            closeButton:"Close"
        });
        
        
    } else if(thisRel.duid != data['user_id'] && parseInt(data['reservation_id'],10)){
        // Related user
        
        $(document).foreTeesModal("alertNotice", {
            width:700,
            title:"Remove member from reservation",
            message_head: 'Are you sure you want to remove '+thisRel.orig_name+' from this reservation?',
            closeButton:"Cancel",
            continueButton: 'Remove',
            allowContinue: true,
            data: data,
            thisRel: thisRel,
            continueAction: function(modalObj, option){
                modalObj.ftDialog("close");
                $('#cancel').attr('disabled', 'disabled');
                $('#message').html("Removing...");
                option.data['cancel_id'] = option.data['reservation_id'];
                delete option.data['reservation_id'];
                option.data['_method'] = 'DELETE';
                option.data['reservation_cancellation_reason_id'] = 2;
                option.data['reservation_cancellation_comments'] = 'Removed by owner';
                option.data['reservation[organization_id]'] = option.data.organization_id;
                $(document).foreTeesModal("pleaseWait");
                //console.log(option.data);
                $.ajax({
                    type: "POST", 
                    url: "/v5/dprox.php", 
                    data: option.data, 
                    dataType: "xml", 
                    success: function(response) {
                        $(document).foreTeesModal("pleaseWait","close");
                        var ok = $('success', response).text();
                        if(ok == '1'){
                            $('#message').html("<h2>"+option.thisRel.orig_name+" Has Been Removed</h2>")
                            //.fadeIn(1500, function() {
                            //    $('#message').append("Refreshing Reservation...");
                            //    window.location.href='Member_teelist?activity_id=9999';
                            //});
                            ftRemoveReservationRelationSlot(option.thisRel.row_number);
                        }else{
                            $('error', response).each(function(id){
                                if(id==0)$('#message').html('<h2>Error Encountered</h2>');
                                var errMsg = $(response).find('error').eq(id).text();
                                $('#message').append(errMsg+'<br>');
                                $('#cancel').removeAttr('disabled');
                            })
                        }
                    }, 
                    error: function(xhr, ajaxOptions, thrownError) {
                        $(document).foreTeesModal("pleaseWait","close");
                        $('#message').html("<h2>Unexpected Error</h2>")
                        $('#message').append("Error: " + thrownError);
                        $('#cancel').removeAttr('disabled');
                    }
                });
            }
        });
        
        
    } else if(thisRel.duid == parseInt(data['user_id'],10)){
        // Master user
        var users = ftGetReservedDiningUsers();
        if(users.length){
            // Remove first, master, user
            users.shift();
        }
        if(!users.length){
            $(document).foreTeesModal("alertNotice", {
                width:700,
                title:"No members found",
                message_head: 'To remove yourself from this reservation you must either cancel the entire reservation, or add another member to the reservation.',
                closeButton:"Close"
            });
            return false;
        } else {
            var optionHtml = [];
            for(var i = 0; i < users.length; i++){
                optionHtml.push('<option value="'+users[i].rel_id+'">'+users[i].name+'</option>');
            }
            var selectHtml = '<label style="display:inline-block;" for="new_reservation_delegate">New owner: </label><select id="new_reservation_delegate" name="new_reservation_delegate">'+optionHtml.join('')+'</select>';
            $(document).foreTeesModal("alertNotice", {
                width:700,
                title:"Delegate reservation to another member",
                message_head: 'To remove yourself from this reservation, you must select another member.',
                message: selectHtml,
                closeButton:"Cancel",
                continueButton: 'Delegate',
                allowContinue: true,
                data: data,
                continueAction: function(modalObj, option){
                    modalObj.ftDialog("close");
                    $('#cancel').attr('disabled', 'disabled');
                    $('#message').html("Removing...");
                    var selectVal = $('select[name="new_reservation_delegate"]').val();
                    option.data['new_master_id'] = selectVal;
                    option.data['_method'] = 'DELETE';
                    option.data['reservation_cancellation_reason_id'] = 2;
                    option.data['reservation_cancellation_comments'] = 'Removed and redelegated';
                    option.data['reservation[organization_id]'] = option.data.organization_id;
                    $(document).foreTeesModal("pleaseWait");
                    //console.log(option.data);
                    $.ajax({
                        type: "POST", 
                        url: "/v5/dprox.php", 
                        data: option.data, 
                        dataType: "xml", 
                        beforeSend: function(x) { 
                            if(x && x.overrideMimeType) { 
                                x.overrideMimeType("application/xml;charset=UTF-8"); 
                            }
                        }, 
                        success: function(response) {
                            $(document).foreTeesModal("pleaseWait","close");
                            var ok = $('success', response).text();
                            if(ok == '1'){
                                $('#message').html("<h2>Person Has Been Removed</h2>")
                                .fadeIn(1500, function() {
                                    $('#message').append("Please Wait...");
                                    //window.location.href='Member_teelist?activity_id=9999';
                                    ftReturnToCallerPage();
                                });
                            }else{
                                $('error', response).each(function(id){
                                    if(id==0)$('#message').html('<h2>Error Encountered</h2>');
                                    var errMsg = $(response).find('error').eq(id).text();
                                    $('#message').append(errMsg+'<br>');
                                    $('#cancel').removeAttr('disabled');
                                })
                            }
                        }, 
                        error: function(xhr, ajaxOptions, thrownError) {
                            $(document).foreTeesModal("pleaseWait","close");
                            $('#message').html("<h2>Unexpected Error</h2>")
                            $('#message').append("Error: " + thrownError);
                            $('#cancel').removeAttr('disabled');
                        }
                    });
                }
            });
        }
    }
}

/*************************************************************
*
* Misc jQuery plugins
*  
*************************************************************/

// Highlight an item by pulsing the background color for a short time
jQuery.fn.foreTeesHighlight = function(color, duration) {
    $(this).stop(true,true);
    var oBgColor = $(this).css("background-color");
    $(this).data("ft-highlightOBgColor", oBgColor);
    $(this).css("background-color", color).animate(
    {
        backgroundColor:oBgColor
    },
    {
        duration: duration,
        complete: function(){
            $(this).css("background-color", oBgColor);
        },
        step: function(){
            $(this).children().addClass("refresh-element").removeClass("refresh-element");
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


/**
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
