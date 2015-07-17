/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


var ftapi = (function(){
    
    var _settings = {
        auth_token:null,
        club_date:null
    };
    
    var _commandQueue = {
        timer:null,
        commands:[],
        options:{}
    };
    
    
    function _promptYesNo(options){
        
        var onCancel = options.onCancel, 
            onContinue = options.onContinue, 
            cancelButton = options.cancelButton,
            continueButton = options.continueButton,
            prompt = options.prompt,
            message = options.message;
        
        if($.fn.foreTeesModal){
            cancelButton = cancelButton?cancelButton:'No';
            continueButton = continueButton?continueButton:'Yes';
            var fixDatesRgxp = /([0-9]{4}[\/\-][0-9]{1,2}[\/\-][0-9]{1,2}|[0-9]{1,2}[\/\-][0-9]{1,2}[\/\-][0-9]{4})/g, 
            fixDatesRpl = '<span style="white-space: nowrap;">$1</span>';

            var opt = {
                title:options.title,
                alertMode:false,
                message:message.replace(fixDatesRgxp,fixDatesRpl).replace(/\n/g,"<br/>"),
                messageObject:'<div class="sub_instructions">'+prompt.replace(fixDatesRgxp,fixDatesRpl)+'</div>',
                closeButton:cancelButton,
                onClose:function($modalObj){
                    if(typeof onCancel == "function"){
                        onCancel();
                    }
                },
                allowContinue:true,
                continueButton:continueButton,
                continueAction:function($modalObj){
                    if(typeof onContinue == "function"){
                        onContinue();
                    }
                    $modalObj.dialog("close");
                }
            }
            $('body').foreTeesModal('alertNotice',opt);
        } else {
            var result = confirm(options.title+"\n\n"+message+"\n\n"+prompt);
            if(result && typeof onContinue == "function"){
                onContinue();
            } else if(!result && typeof onCancel == "function"){
                onCancel();
            }
        }
    }
    
    
    function _encapsulateCommand(command){
        if(!$.isArray(command)){
            if(typeof command == "string"){
                // Wrap sting command in an object
                command = {command:command};
            }
            // Wrap single command object in an array
            command = [command];
        }
        return command;
    }
    
    var _error = function(errorMessage, command, data){
        var $body = $('body'), mparts = errorMessage.split(':'), mtitle = mparts.shift(), m = mparts.join(':').trim();
        if(!m){
            m = mtitle;
            mtitle = "Error";
        }
        if(data && data.continue_prompt){
            _promptYesNo({
                title:"Warning:",
                message:m,
                prompt:data.continue_prompt,
                onContinue:data.onContinue,
                onCancel:data.onCancel
            })
        } else {
            if($.fn.foreTeesModal){
                $body.foreTeesModal('alertNotice',{title:mtitle,message_head:m.replace(/\n/g,"<br/>")});
            } else {
                alert(mtitle+": "+m);
            }
        }
    }
    
    
    var _xhrError = function(jqXHR, command){
        _error("API Error:\n\n"+jqXHR.responseText, command);
    }
    
    var _uri = function(command, options){
        if(!options){
            options = {};
        }
        return "API?commands=" + encodeURIComponent(JSON.stringify(_filterCommands(command,options)));
    }
    
    var _uriWithToken = function(command, options){
        if(!options){
            options = {};
        }
        return _setUriAuthToken("API?commands=" + encodeURIComponent(JSON.stringify(_filterCommands(command,options))),options.singleUseKey);
    }
    
    // Get URI for export command
    var _exportUri = function(filename, contentType, excludeApiToken){
        
        var result = "API?method=EXPORT&command=blob";
        if(!excludeApiToken){
            result =  _setUriAuthToken(result);
        }
        if(contentType){
            result += "&content_type="+encodeURIComponent(contentType);
        }
        if(filename){
            result += "&filename="+encodeURIComponent(filename);
        }
        return result;
    }
    
    var _filterCommands = function(command, options){
        if(!options){
            options = {};
        }

        if(!$.isArray(command)){
            // Wrap single command object in an array
            if(typeof command == "string"){
                command = {command:command};
            }
            command = [command];
        }

        var remoteRequest = $.extend(true,[],command);
        // remove local references that we don't want to send to the remote API
        var filters = ['success','error','beforeError','onSuccess','onError','target','ccq'];
        remoteRequest.forEach(function(c){
            filters.forEach(function(f){
                delete c[f]
            });
        });
        
        return remoteRequest;
    }
    
    var _setUriAuthToken = function(uri, singleUseKey){
        var authToken = _settings.auth_token;
        //console.log("URI Auth Token:"+authToken);
        if(typeof singleUseKey == "string" && singleUseKey.length > 1){
            uri += "&sua_key="+encodeURIComponent(singleUseKey);
        } else if(typeof authToken == "string" && authToken.length > 1){
            //console.log("Using Auth Token:"+authToken);
            uri += "&auth_token="+encodeURIComponent(authToken);
        }
        return uri;
    }
    
    var _setHeaderAuthToken = function(jqHXR, singleUseKey){
        var authToken = _settings.auth_token;
        //console.log("URI Auth Token:"+authToken);
        if(typeof singleUseKey == "string" && singleUseKey.length > 1){
            jqHXR.setRequestHeader('X-FtApi-SingleUseToken', singleUseKey);
        } else if(typeof authToken == "string" && authToken.length > 1){
            jqHXR.setRequestHeader('X-FtApi-Token', authToken);
        }
    }
    
    var _processResponse = function(data){
        var authToken = data.auth_token;
        //console.log("Checking Auth Token:"+authToken);
        if(typeof authToken == "string" && authToken.length > 1){
            //console.log("Setting Auth Token:"+authToken);
            _settings.auth_token = authToken;
        }
        
    }
    
    var _get = function(command, options){
        
        command = _encapsulateCommand(command);
        
        if(options === true){
            options = {ccq:true};
        }
        if(!options){
            options = {};
        }
        if(typeof options.success == "function"){
            options.ccq = false; // global success processing doesn't support dynamic Client Command Queue
        }
        //options.ccq = false; // Force command queuing off
        
        if(options.ccq){
            // Queue command(s) for later (Queue will run after current javascript thread completes)
            
            // Clear previous queue timer
            clearTimeout(_commandQueue.timer);
            // make sure the commands we queue don't try to triggger another queue when it comes time to execute the queue
            delete options.ccq;
            // CCQ'd comands must be async
            delete options.sync;
            // Merge any options from this request with any previous 
            // (should be nothing to merge 
            //   -- probably should do away with this, global options generally don't work well with CCQ)
            for(var k in options){
                if(typeof _commandQueue.options.k == "undefined"){
                    _commandQueue.options.k = options[k];
                }
            }
            // Add command(s) to the queue
            command.forEach(function(c){
                //console.log("Queueing:"+c.command);
                // Use a unique string for each command's request id
                c.request_id = new Date().getTime() + ':' + _commandQueue.commands.length + ':' + c.command;
                _commandQueue.commands.push(c);
            });
            // Set a new queue timer (setting to 1 should execute the timer after the current javascript thread ends)
            _commandQueue.timer = setTimeout(function(){
                //console.log("Executng Queue:");
                //console.log(_commandQueue.commands);
                _get(_commandQueue.commands, _commandQueue.options);
                _commandQueue.commands = [];
                _commandQueue.options = {};
            },1);
            return;
        }
        var onError = options.error, beforeError = options.beforeError, gOnSuccess = options.success;
        $.ajax("API?method=GET",{
            cache: false,
            async: (!options.sync),
            beforeSend:function(jqHXR){
                _setHeaderAuthToken(jqHXR, options.singleUseKey);
            },
            dataType: 'json', // We're expecting a JSON response
            type:'POST', // Post commands in request body incase command queue goes over ~2kb URI limmit of some browsers.
            processData: false, // Don't let jQuery modify the request (why didn't they call this setting "processRequest"?)
            data:JSON.stringify(_filterCommands(command,options)), // Pass commands in request body as JSON
            contentType: 'application/json', // Signify that we're sending JSON data in the request body
            error: function(xhr, status, error){
                // Error
                if(typeof options.error == "function"){
                    options.error(xhr.responseText, command);
                } else {
                    if(typeof options.beforeError == "function"){
                        options.beforeError(xhr.responseText, command);
                    }
                    //alert("API Error:\n"+xhr.responseText);
                    if(typeof beforeError == "function"){
                        beforeError(xhr.responseText, command);
                    }
                    _error("API Error:\n\n"+xhr.responseText);
                }
            },
            success: function(data){
                if(data.date){
                    _updateClubDate(data.date);
                }
                if(!data.success){
                    // Error
                    if(typeof onError == "function"){
                        onError(data.error, command, data);
                    } else {
                        if(typeof beforeError == "function"){
                            beforeError(data.error, command, data);
                        }
                        //alert("Unable to complete request:\n\n"+data.error);
                        _error("Unable to complete request:\n\n"+data.error, command, data);
                    }
                } else {
                    // Success
                    _processResponse(data);
                    command.forEach(function(c){
                        // If a request ID exists for this command, use that for the response key, else use the command name
                        var cid = c.request_id?c.request_id:c.command, onSuccess = c.success;
                        
                        // If a target object was specified, store the response in the target
                        if(c.target){
                            c.target[c.command] = data.results[cid];
                        }
                        if(options.target){
                            options.target[c.command] = data.results[cid];
                        }
                        // If a success method was specified for this command request, execute it.
                        if(typeof onSuccess == "function"){
                            onSuccess(data.results[cid], c);
                        }
                    });
                    // If a global success method was specified, execute it.
                    if(typeof gOnSuccess == "function"){
                        gOnSuccess(data.results, command);
                    }
                }
            }
        });
    };
    
    var _put = function(o){

        var command = o.command, beforeError = o.beforeError, onError = o.error, continue_parameter = o.continue_parameter, url = "API?method=PUT&command="+command;
        if(continue_parameter){
            url += "&" + continue_parameter + "=" + o.continue_value;
        }
        $.ajax(url,{
            cache: false,
            async: (!o.sync),
            dataType: 'json',
            type:'POST',
            //type:'PUT',
            processData: false,
            beforeSend:function(jqHXR){
                _setHeaderAuthToken(jqHXR, o.singleUseKey);
            },
            data:JSON.stringify(o.data),
            contentType: 'application/json',
            error: function(xhr, status, error){
                // Error
                var errorTxt = "API Error\n"+xhr.responseText;
                if(typeof onError == "function"){
                    onError(errorTxt, xhr.responseText, command, {}, o);
                } else {
                    //alert(errorTxt);
                    if(typeof beforeError == "function"){
                            beforeError(errorTxt, command);
                        }
                    _error(errorTxt, command);
                }
            },
            success: function(data){
                if(!data.success){
                    // Error
                    var errorTxt = "Unable to update record:\n\n"+data.error;
                    if(typeof onError == "function"){
                        onError(errorTxt, data.error, command, data, o);
                    } else {
                        //alert(errorTxt);
                        if(typeof beforeError == "function"){
                            beforeError(data.error, command, data);
                        }
                        data.onContinue = function(){
                            // Retry this command
                            if(data.continue_parameter){
                                o.continue_parameter = data.continue_parameter;
                                o.continue_value = true;
                            }
                            _put(o);
                        }
                        _error(errorTxt, command, data);
                    }
                } else {
                    // Success
                    _processResponse(data);
                    if(typeof o.success == "function"){
                        o.success(data.results[command], command, o);
                    }
                }
            }
        });
    };
    
    var _delete = function(o){

        var command = o.command, beforeError = o.beforeError, onError = o.error;
        $.ajax("API?method=DELETE&command="+command+"&id="+o.id,{
            cache: false,
            async: (!o.sync),
            dataType: 'json',
            type:'POST', //type:'DELETE',
            beforeSend:function(jqHXR){
                _setHeaderAuthToken(jqHXR, o.singleUseKey);
            },
            //processData: false,
            //data:JSON.stringify(o.data),
            contentType: 'application/json',
            error: function(xhr, status, error){
                // Error
                if(typeof onError == "function"){
                    onError(xhr.responseText, command, {}, o);
                } else {
                    //alert("API Error\n"+xhr.responseText);
                    if(typeof beforeError == "function"){
                            beforeError(xhr.responseText, command);
                        }
                    _error("API Error:\n\n"+xhr.responseText, command);
                }
            },
            success: function(data){
                if(!data.success){
                    // Error
                    if(typeof onError == "function"){
                        onError(data.error, command, data, o);
                    } else {
                        //alert("Unable to delete record:\n\n"+data.error);
                        if(typeof beforeError == "function"){
                            beforeError(data.error, command, data);
                        }
                        _error("Unable to delete record:\n\n"+data.error,command,data);
                    }
                } else {
                    // Success
                    _processResponse(data);
                    if(typeof o.success == "function"){
                        o.success(data.results, command, o);
                    }
                }
            }
        });
    };
    
    var _updateClubDate = function(sdate){
        if(sdate && ftcalendar && ftcalendar.stringToDate){
            _settings.club_date = ftcalendar.stringToDate(sdate);
        }
    }
    
    var _getClubDate = function(){
        return _settings.club_date;
    }
    
    var _downloadData = function(filename, contentType, content){
        // TODO:  Add native client downloading for browsers that support Blob, to avoid round trip call to server.
        var url = _exportUri(null, contentType, true);
        $.ajax({
            url: url + "&filename="+encodeURIComponent(filename),
            type: "POST",
            cache: false,
            beforeSend:function(jqHXR){
                _setHeaderAuthToken(jqHXR);
            },
            data: {
                content: content
            },
            success: function(tempfilename) {
                var downloadurl = url + (((url.indexOf("?") > 0) ? "&" : "?") + "filename=" + tempfilename);
                _downloadFile(downloadurl);
                
            },
            error: function(xhr, status, error){
                _error(xhr.responseText, "EXPORT blob");
            }
        })
    }
    
    var _downloadFile = function(url){
        $(document.body).append("<iframe height='0' width='0' frameborder='0'  src=" + url + "></iframe>")
    }
    
    // See if two objects have the same values
    var _compareObjects = function(obj1, obj2) {
        if(typeof obj1 == "object"){
            if(typeof obj1 != typeof obj2){
                //console.log("Objects are differnet types");
                //console.log(obj1);
                //console.log(obj2);
                return false;
            }
            var sub1, sub2, i;
            if($.isArray(obj1)){
                // Array
                if(!$.isArray(obj2)){
                    //console.log("Both are not arrays");
                    //console.log(obj1);
                    //console.log(obj2);
                    return false
                }
                if (!obj2 || obj1.length != obj2.length) {
                    //console.log("Array length not the same");
                    //console.log(obj1);
                    //console.log(obj2);
                    return false;
                }
                for (i = 0; i < obj1.length; ++i) {
                    sub1 = obj1[i], sub2 = obj2[i];
                    if(typeof sub1 == "object"){
                        if (sub2 && typeof sub1.getTime == 'function' && typeof sub2.getTime == 'function') {
                            // Dates
                            if(sub1.getTime() != sub2.getTime()){
                                //console.log("Dates do not match");
                                //console.log(sub1);
                                //console.log(sub2)
                                return false;
                            }
                        } else if(!(_compareObjects(sub1, sub2))){
                            return false;
                        }
                    } else if (typeof sub1 == "function" || typeof sub2 == "function" ) {
                        // Do nothing. Allow.
                    } else if (sub1 !== sub2) {
                        //console.log("Values from " + i + " not the same");
                        //console.log(typeof sub1);
                        //console.log(typeof sub2);
                        //console.log(sub1);
                        //console.log(sub2);
                        //console.log(obj1);
                        //console.log(obj2);
                        return false;
                    }
                }
                return true;
            } else {
                // Object
                var count1 = 0, count2 = 0;
                for(i in obj1){
                    sub1 = obj1[i], sub2 = obj2[i];
                    if(typeof sub1 == "object"){
                        if (sub2 && typeof sub1.getTime == 'function' && typeof sub2.getTime == 'function') {
                            // Dates
                            if(sub1.getTime() != sub2.getTime()){
                                //console.log("Dates do not match");
                                //console.log(sub1);
                                //console.log(sub2)
                                return false;
                            }
                        } else if(!(_compareObjects(sub1, sub2))){
                            return false;
                        }
                    } else if (typeof sub1 == "function" || typeof sub2 == "function" ) {
                        // Do nothing. Allow.
                    } else if (sub1 !== sub2) {
                        //console.log("Values from " + i + " not the same");
                        //console.log(typeof sub1);
                        //console.log(typeof sub2);
                        //console.log(sub1);
                        //console.log(sub2);
                        //console.log(obj1);
                        //console.log(obj2);
                        return false;
                    }
                    count1 ++;
                }
                for(i in obj2){
                    count2 ++;
                }
                if(count1 != count2){
                    //console.log("Object length not the same");
                    //console.log(obj1);
                    //console.log(obj2);
                    return false;
                }
                return true;
            }
        } else if (obj1 && obj1 && typeof obj1.getTime == 'function' && typeof obj1.getTime == 'function') {
            // Dates
            if(obj1.getTime() != obj2.getTime()){
                //console.log("Dates do not match");
                //console.log(obj1);
                //console.log(obj2)
                return false;
            }
        } else if(obj1 !== obj2){
            //console.log("Values not the same");
            //console.log(obj1);
            //console.log(obj2);
            return false;
        }
        return true;
        
    };
    
    return {
        uri:_uri, 
        exportUri:_exportUri,
        get:_get, 
        put:_put, 
        del:_delete, 
        processResponse:_processResponse, 
        updateClubDate:_updateClubDate, 
        getClubDate:_getClubDate,
        downloadData:_downloadData, 
        downloadFile:_downloadFile, 
        setHeaderAuthToken:_setHeaderAuthToken,
        uriWithToken:_uriWithToken,
        error:_error,
        xhrError:_xhrError,
        compareObjects:_compareObjects
    };
    
})();
/*
// Blob polyfill
Blob = (function() {
  var nativeBlob = Blob;

  // Add unprefixed slice() method.
  if (Blob.prototype.webkitSlice) {
    Blob.prototype.slice = Blob.prototype.webkitSlice;  
  }
  else if (Blob.prototype.mozSlice) {
    Blob.prototype.slice = Blob.prototype.mozSlice;  
  }

  // Temporarily replace Blob() constructor with one that checks support.
  return function(parts, properties) {
    try {
      // Restore native Blob() constructor, so this check is only evaluated once.
      Blob = nativeBlob;
      return new Blob(parts || [], properties || {});
    }
    catch (e) {
      // If construction fails provide one that uses BlobBuilder.
      Blob = function (parts, properties) {
        var bb = new (WebKitBlobBuilder || MozBlobBuilder), i;
        for (i in parts) {
          bb.append(parts[i]);
        }
        return bb.getBlob(properties && properties.type ? properties.type : undefined);
      };
    }        
  };
}());
*/