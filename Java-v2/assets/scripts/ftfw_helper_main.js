/*

ForeTees / Flexscape Integration Helper File

*/

var JSON;if(!JSON){JSON={}}(function(){function f(n){return n<10?"0"+n:n}if(typeof Date.prototype.toJSON!=="function"){Date.prototype.toJSON=function(key){return isFinite(this.valueOf())?this.getUTCFullYear()+"-"+f(this.getUTCMonth()+1)+"-"+f(this.getUTCDate())+"T"+f(this.getUTCHours())+":"+f(this.getUTCMinutes())+":"+f(this.getUTCSeconds())+"Z":null};String.prototype.toJSON=Number.prototype.toJSON=Boolean.prototype.toJSON=function(key){return this.valueOf()}}var cx=/[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,escapable=/[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,gap,indent,meta={"\b":"\\b","\t":"\\t","\n":"\\n","\f":"\\f","\r":"\\r",'"':'\\"',"\\":"\\\\"},rep;function quote(string){escapable.lastIndex=0;return escapable.test(string)?'"'+string.replace(escapable,function(a){var c=meta[a];return typeof c==="string"?c:"\\u"+("0000"+a.charCodeAt(0).toString(16)).slice(-4)})+'"':'"'+string+'"'}function str(key,holder){var i,k,v,length,mind=gap,partial,value=holder[key];if(value&&typeof value==="object"&&typeof value.toJSON==="function"){value=value.toJSON(key)}if(typeof rep==="function"){value=rep.call(holder,key,value)}switch(typeof value){case"string":return quote(value);case"number":return isFinite(value)?String(value):"null";case"boolean":case"null":return String(value);case"object":if(!value){return"null"}gap+=indent;partial=[];if(Object.prototype.toString.apply(value)==="[object Array]"){length=value.length;for(i=0;i<length;i+=1){partial[i]=str(i,value)||"null"}v=partial.length===0?"[]":gap?"[\n"+gap+partial.join(",\n"+gap)+"\n"+mind+"]":"["+partial.join(",")+"]";gap=mind;return v}if(rep&&typeof rep==="object"){length=rep.length;for(i=0;i<length;i+=1){if(typeof rep[i]==="string"){k=rep[i];v=str(k,value);if(v){partial.push(quote(k)+(gap?": ":":")+v)}}}}else{for(k in value){if(Object.prototype.hasOwnProperty.call(value,k)){v=str(k,value);if(v){partial.push(quote(k)+(gap?": ":":")+v)}}}}v=partial.length===0?"{}":gap?"{\n"+gap+partial.join(",\n"+gap)+"\n"+mind+"}":"{"+partial.join(",")+"}";gap=mind;return v}}if(typeof JSON.stringify!=="function"){JSON.stringify=function(value,replacer,space){var i;gap="";indent="";if(typeof space==="number"){for(i=0;i<space;i+=1){indent+=" "}}else{if(typeof space==="string"){indent=space}}rep=replacer;if(replacer&&typeof replacer!=="function"&&(typeof replacer!=="object"||typeof replacer.length!=="number")){throw new Error("JSON.stringify")}return str("",{"":value})}}if(typeof JSON.parse!=="function"){JSON.parse=function(text,reviver){var j;function walk(holder,key){var k,v,value=holder[key];if(value&&typeof value==="object"){for(k in value){if(Object.prototype.hasOwnProperty.call(value,k)){v=walk(value,k);if(v!==undefined){value[k]=v}else{delete value[k]}}}}return reviver.call(holder,key,value)}text=String(text);cx.lastIndex=0;if(cx.test(text)){text=text.replace(cx,function(a){return"\\u"+("0000"+a.charCodeAt(0).toString(16)).slice(-4)})}if(/^[\],:{}\s]*$/.test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,"@").replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,"]").replace(/(?:^|:|,)(?:\s*\[)+/g,""))){j=eval("("+text+")");return typeof reviver==="function"?walk({"":j},""):j}throw new SyntaxError("JSON.parse")}}}());

var ftVars = {resizeTimer:false,baseUrl:null};

// Add Date.now() to ie8/7
Date.now = Date.now || function() {return +new Date;};

$(document).ready(function() {
    $('iframe.ftStaticLink').each(function(){
        var url = $(this).attr('data-fturl');
        var baseUrl = ftLinkData.baseUrl;
        var activity = ftLinkData.defaultActivity;
        var mode = ftLinkData.defaultMode;
        if(typeof mode == "undefined"){
            mode = 0;
        }
        mode = parseInt(mode,10);
        if($(this).attr('data-ftactivity')){
            activity = $(this).attr('data-ftactivity');
        }
        if($(this).attr('data-ftsetmodebit')){
            var cmode = parseInt($(this).attr('data-ftsetmodebit'),10);
            mode = mode | cmode;
        }
        if($(this).attr('data-ftclearmodebit')){
            var cmode = parseInt($(this).attr('data-ftclearmodebit'),10);
            mode = mode & (~cmode);
        }
        if($(this).attr('data-ftforcemode')){
            mode = parseInt($(this).attr('data-ftforcemode'),10);
        }
        if(activity.match(/^[0-9]+$/)){
            activity = 'flxrez' + parseInt(activity,10);
        }
        baseUrl += ftLinkData.club+'_'+activity+'_m'+mode+'/';
        //console.log('base:'+baseUrl);
        //console.log('url:'+url);
        
        var o = {
            base_url: baseUrl,
            url: url
        }
        ftSSOLink(ftSSOKey, ftSSOIV, o);
    });
    
    // Check if we have a custom external css file and need to remove the premier forced css file
    if($('head link[href$="/premier_external.css"]').length){
        $('#fwContent link[href$="/premier_external.css"]').remove();
    }
    
});

function ftMessageHandler(e) {
   var data = {};
    try{
        data=JSON.parse(e.data);
    }catch(e){
        data={};
    }
    //console.log('WindowPostMessage:');
    //console.log(data);
    if(data.command){
        switch (data.command) {
            case 'ftGoBack':
                ftGoBack(data.message);
                break;
            case 'ftSendSize':
                ftSendSize(e.source);
                break;
            case 'ftDoResize':
                ftSendSize(e.source);
                break;
            case 'ftResizeIframe':
                var ifr = document.getElementById('ifrforetees');
                ftResizeIframe(ifr, data.message);
                ftSendSize(e.source);
                ftPostMessage(ifr.contentWindow,'didResize',false);
                break;
            case 'ftResizeIframeNoCallback':
                var ifr = document.getElementById('ifrforetees');
                ftResizeIframe(ifr, data.message);
                ftSendSize(e.source);
                break;
                //ftPostMessage(ifr.contentWindow,'didResize',false);
            case 'ftTPA':  // use top window to authenticate foretees as visited
                ftTPA();
                break;

        }
    }
}

function ftGoBack(m){
    var calButton = $('span#calButtonBack a').first();
    if(calButton.length){
        //console.log('click buttons:'+calButton.length);
        
        //console.log('clicking go back');
        //console.log("attr:"+calButton.attr('href'));
        if(calButton.get(0).click){
            calButton.get(0).click();
        } else {
            //console.log('not a clickable button');
            window.location.href = calButton.attr('href');
        }
        
        //console.log('clicked go back');
    } else {
        //console.log('do history back');
        if(!!m.forceUrl && m.forceUrl.length){
            document.getElementById('ifrforetees').contentWindow.location = m.forceUrl;
        }else{
            window.history.back();
        }
    }
}

function ftResizeIframe(ifr,o){
    if(ifr){
        $(ifr).css('height',(o.height)+'px');
        
    }
}

function ftSendSize(win){ 
    var ifr = document.getElementById('ifrforetees');
    if(win){
        var viewheight = $(window).height();
        if(window.innerHeight){
            viewheight = window.innerHeight;
        }
        ftPostMessage(win,'ftParentSize',{offsetTop:$(ifr).offset().top,scrollTop:$(window).scrollTop(),height:viewheight});
    }
}

function ftRequestResize(win){ 
    if(win){
        //console.log('resize request');
        ftPostMessage(win,'ftRequestResize',false);
    } 
}

function ftPostMessage(win,command,message){
    if(win && win.postMessage){
        win.postMessage(JSON.stringify({command:command,message:message}),'*');
    } else {
        // No support for post message
    }
}

//window.addEventListener('message', ftMessageHandler, false);
$(window).bind('message', function(e){
    ftMessageHandler(e.originalEvent);
});
$(window).scroll(function(){
    var ifr = document.getElementById('ifrforetees');
    if(ifr && ifr.contentWindow){
        ftSendSize(ifr.contentWindow);
    }
});

$(window).resize(function(){
    var ifr = document.getElementById('ifrforetees');
    if(ifr && ifr.contentWindow){
        ftSendSize(ifr.contentWindow);
        //ftRequestResize(ifr.contentWindow);
        if(ftVars.resizeTimer){
            clearTimeout(ftVars.resizeTimer);
        }
        ftVars.resizeTimer = setTimeout(function(){ftPostMessage(ifr.contentWindow,'ftDidResize',false);},200);
    }
});

function ftSSOLink(uid, iv, o){
    if (o.base_url.length && o.url.length) {

        if(typeof ftLinkData != "undefined" && ftLinkData.forceMode){
            // Parse base url and reconstruct with forced mode
            var test = /\/([a-zA-Z0-9]+_[a-zA-Z0-9]+_m[0-9]+)\/$/g;
            var cama = test.exec(o.base_url);
            if(cama && cama.length == 2){
                var parts = cama[1].split("_");
                var club, activity, mode;
                club = parts[0];
                activity = parts[1];
                test = /^m([0-9]+)$/g;
                var arr = test.exec(parts[2]);
                if(arr && arr.length == 2){
                    mode = arr[1];
                }
                if(club.length && activity.length && mode.length){
                    o.base_url = o.base_url.replace(cama[1],club+"_"+activity+"_m"+ftLinkData.forceMode);
                }
            }
        }
        ftVars.baseUrl = o.base_url;
        ftVars.uid = uid;
        ftVars.iv = iv;
        
        var url = o.base_url + o.url;
        /*
        if(!isSafari()){
            url = ftUpdateQueryString('sso_uid', uid, url);
            url = ftUpdateQueryString('sso_iv', iv, url);
            ftSetIframe(url,o.data);
        }else{
            // If we're safari, we need to check if foretees has been "visited"
            // and if not, "visit" foretees.com using the top window in order to
            // authenticate use of cookies in an iframe
            */
           ftSSO({
                baseUrl:o.base_url,
                data:{uid:uid,iv:iv},
                success: function(r){
                    //console.log(r);
                    //console.log('tpa:'+r.foreTeesSSOResp.sessionUuid);
                    if(!ftGetUriParam('ft_tpa_complete')){
                        //console.log("tpa_auth_check");
                        url = ftUpdateQueryString('sso_uid', uid, url);
                        url = ftUpdateQueryString('sso_iv', iv, url);
                        url = ftUpdateQueryString('sso_tpa', r.foreTeesSSOResp.sessionUuid, url);
                    } else {
                        //console.log("tpa_complete_mode");
                        // We should already have a session, don't send the iv and uid.
                    }
                    ftSetIframe(url,o.data);
                },
                error: function(e){
                    //console.log(e);
                    if(e.foreTeesErrorResp){
                        alert('Unable to sign in to foretees.  Error: '+e.foreTeesErrorResp.errorCode+'; '+e.foreTeesErrorResp.errorMessage);
                    } else {
                        alert('Unable to sign in to foretees.  Error: unknown');
                    }

                }
            }); 
            /*
        }
        */

        // Build form, and post it (this was replaced by above URI extension).
        //var formObj = $('<form action="' + url + '" method="post" target="ifrforetees">' + ftDataToForm(o.data, { }) + '</form>');
        //$("body").append(formObj);
        //formObj.submit();

        return false;
    } else {
        alert("Unable to establish base_url for event.");
        return false;
    }
}

function ftSetIframe(url, data){
    // Extend URI with data and update iframe location
    url = ftDataToUri(url, data);
    url = ftUpdateQueryString('_', Date.now(), url); // Stop request from hitting browser cache
    var ifr = $('iframe#ifrforetees');
    if(ftIsMobile('ios')){
        // Hack to workaround ios iframe sizing issue
        ifr.attr('scrolling','no');
        ifr.css('width','10px').css('min-width','100%');
    }
    ifr.get(0).contentWindow.location.replace(url);
}

function ftSSO(new_opt){
    var defaults = {
        baseUrl:'',
        url:'Common_webapi?sso',
        success:function(r){},
        error:function(e){},
        data:{
            uid:'',
            type:'sso',
            iv:''
        }
    }
    var opt = $.extend(true, {}, defaults, new_opt );
    $.ajax({
         url: opt.baseUrl+opt.url,
         contentType: "application/json",
         dataType: 'jsonp',
         data: opt.data,
         success: function(r) {
            if(r.foreTeesSSOResp && r.foreTeesSSOResp.valid){
                opt.success(r);
            } else {
                opt.error(r);
            }
         },
         error: function(e) {
            // Bad response from server
            opt.error(e);
         }
     });
}

function ftUpdateQueryString(key, value, url) {
    if (!url) url = window.location.href;
    var re = new RegExp("([?|&])" + key + "=.*?(&|#|$)(.*)", "gi");

    if (re.test(url)) {
        if (typeof value !== 'undefined' && value !== null)
            return url.replace(re, '$1' + encodeURIComponent(key) + "=" + encodeURIComponent(value) + '$2$3');
        else {
            return url.replace(re, '$1$3').replace(/(&|\?)$/, '');
        }
    }
    else {
        if (typeof value !== 'undefined' && value !== null) {
            var separator = url.indexOf('?') !== -1 ? '&' : '?',
                hash = url.split('#');
            url = hash[0] + separator + encodeURIComponent(key) + '=' + encodeURIComponent(value);
            if (hash[1]) url += '#' + hash[1];
            return url;
        }
        else
            return url;
    }
}

// Embed key/string pair in an object to a url as uri parameters.
function ftDataToUri(url, obj, exclude) {
    if (exclude === undefined || exclude == null) {
        exclude = {};
    }
    if(obj){
        for (var key in obj) {
            var item = obj[key];
            if (typeof (item) != "object" && typeof (exclude[key]) == "undefined") {
                url = ftUpdateQueryString(key, item, url);
            }
        }
    }
    return url;
}

function ftEscapeRegExp(str) {
    return str.replace(/[-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
}

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

// Convert key/string pair in an object to a string of hidden form elements.
function ftDataToForm(obj, exclude) {
    var html = "";
    if (exclude === undefined || exclude == null) {
        exclude = {};
    }
    for (var key in obj) {
        var item = obj[key];
        if (typeof (item) != "object" && typeof (exclude[key]) == "undefined") {
            html += '<input type="hidden" name="' + key + '" value="' + item + '">';
        }
    }
    return html;
}

function ftTPA(){
    // We were asked by a foretees iframe to "visit" foretees in order to
    // allow cookies from foretees to be used, getting around the "third party cookie"
    // restriction of newer safari browsers.
    // We'll pass our current url so thirdPartyAuth can pass control back to us when complete
    var url = ftVars.baseUrl + 'thirdPartyAuth';
    url = ftUpdateQueryString('sso_uid', ftVars.uid, url);
    url = ftUpdateQueryString('sso_iv', ftVars.iv, url);
    var ret_url = ftUpdateQueryString('ft_tpa_complete', '1', window.location.href);
    window.location.replace(ftUpdateQueryString('sso_tpa_url', ret_url, url));
}

function isSafari(){
    var ua = navigator.userAgent.toLowerCase(); 
    if (ua.indexOf('safari')!=-1){ 
        if(!ua.indexOf('chrome')  > -1){
            return true;
        }
    }
    return false;
}

function ftPushScriptFile(filename, filetype){
    var html = '';
    if (filetype=="js"){
        html = '<script language="javascript" src="'+filename+'"></script>';
    }
    else if (filetype=="css"){
        html = '<link rel="stylesheet" href="'+filename+'" type="text/css">';
    }
    document.write(html);
}

function ftPushScript(script, type){
    var html = '';
    if (type=="js"){ 
        html = '<script>'+script+'</script>';
    }
    else if (type=="css"){
        html = '<style>'+script+'</style>';
    }
    document.write(html);
}

function ftHash(str){
    // use 
    // console.log(ftHash(window.location.host));
    // at console of host to get host hash.
    var hash = 0;
    if (str.length == 0) return hash;
    for (var i = 0; i < str.length; i++) {
        var chr = str.charCodeAt(i);
        hash = ((hash<<5)-hash)+chr;
        hash = hash & hash;
    }
    return hash;
}

switch(ftHash(window.location.host)){
    case -568523934: // hash of foretees.flexdemo.com
        //ftPushScript('div#innernav{display:none;}', 'css')
        break;
    
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
