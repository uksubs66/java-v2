/**************************************
 * 
 * Member calendar plugin
 * 
 * ************************************
 */

(function($){
    
    var pluginData = {
        objectIndex:0,
        jsonRunning:false,
        jsonData:null,
        waitingForModal:false,
        modalObj:null
    };
    
    var options = {
        
        toolTips: {
            tlt:'Available',
            0:'View Only',
            1:'Tee Times Available',
            2:'Lottery'
        },
        lesson: {
            toolTips: {
                1:'Space Available'
            }
        },
    
        jsonSourceUrl:'Member_select?json_mode=true',
        onSelect: function(dateText,inst){
            var calendar_map = pluginData.jsonData;
            //alert(dateText+'|'+$(inst).attr("id"));
            if("date_map" in calendar_map){
                
                calendar_map.selected_date = dateText;
                calendar_map.selected_record = calendar_map.date_map[dateText];
                var form_fields = {};
                var url = ftReplaceKeyInString(calendar_map.callback_url,calendar_map);
                if("callback_form" in calendar_map && $('form[name="'+calendar_map.callback_form+'"]').length){
                    form_fields = $.extend({},form_fields,$('form[name="'+calendar_map.callback_form+'"]').formToObject());
                }
                //console.log(JSON.stringify(form_fields));
                if("callback_field_map" in calendar_map){
                    form_fields = $.extend({},form_fields,ftFillParemetersInObject(calendar_map.callback_field_map,calendar_map));
                }
                
                var method = "get";
                if(calendar_map.callback_method == "post"){
                    method = "post";
                }
                var form = $('<form method="'+method+'" action="'+ftSetJsid(url)+'"></form>');
                $("body").append(form);
                form.append(ftObjectToForm(form_fields));
                
                form.submit();
    
            } else {
                var coursename = "";
                if(typeof($(this).data("courseSelect")) != "undefined"){
                    coursename = $(this).data("courseSelect").val();
                }
                if(calendar_map.IS_TLT){
                    ftChangePage(ftSetJsid("MemberTLT_sheet?calDate="+dateText+"&course="+coursename));
                }else{
                    var consec = "";
                    if ($('#select_times').length) {
                        consec = '&select_times=' + $('#select_times :selected').val();
                    }
                    ftChangePage(ftSetJsid("Member_sheet?calDate="+dateText+"&course="+coursename+((($("#select_jump").is(':checked')) || ftGetUriParam("select_jump", window.location.href ) != null) ? "&select_jump" : ($("body.Member_select").length || ftGetUriParam("show_calendar", window.location.href ) != null?"&show_calendar":""))+consec));
                }
            }
            
        },
        disableOnDayValue: null // If day value is this, it will disable the day in the calendar 
        
    };
    
    var methods = {
        
        init: function(option){

            var currentDate = new Date();
            var calGroup = $(this);

            $(this).each(function(index){

                $.fn.foreTeesMemberCalendar('setOptions',option);               
                var calContainer = $(this);
                var calObj;
                
                if(typeof(calContainer.data("ft-calObject")) == "undefined"){
                    if(calContainer.is("input")){
                        calObj = calContainer;
                    } else {
                        calContainer.css("position", "relative");
                        calObj = $('<div class="foreTeesMemberCalendarObject"></div>');
                        calContainer.append(calObj);
                    }
                    calContainer.data("ft-calObject",calObj);
                } else {
                    calObj = calContainer.data("ft-calObject");
                }
                
                if(typeof(calObj.data("ft-calGroup")) == "undefined"){
                    calContainer.data("ft-calGroup",calGroup);
                }
                
                // Bind the course select list
                if(typeof option =="object" && $(option.courseSelect).length){
                    calObj.data("courseSelect", $(option.courseSelect).first());
                }
              
                // Bind any objects who's click will reload us
                if(typeof option =="object" && $(option.reloadObject).length){
                    $(option.reloadObject).click($.proxy(function(){
                        $(this).foreTeesMemberCalendar("reload");
                        return false;
                    },calContainer));
                }
                
                // Check if we have JSON data, if not get it.   
                //console.log('Checking json data in attribute');
                var result = pluginData.jsonData;
                if(result == null){
                    try {
                        result = JSON.parse(calContainer.attr('data-ftjson'));
                    //console.log('Parsed Json');
                    } catch (e) {
                    //console.log("Json data corrupt");
                    }
                }
                if(result == null){
                    var defaultDate = Date();
                    if(typeof(calContainer.attr('data-ftdefaultdate')) != "undefined" && calContainer.attr('data-ftdefaultdate').length){
                        defaultDate = ftStringDateToDate(calContainer.attr('data-ftdefaultdate'));
                    }
                    var dpopts = {
                        disabled: true, defaultDate: defaultDate
                    };
                    /*
                    if(calContainer.is('input')){
                            dpopts.disabled = false;
                            dpopts.showOn = "both";
                            dpopts.format = 'mm/dd/yyyy';
                            dpopts.buttonImage = "../assets/images/calendar_field.png"
                    }
                    */
                    calObj.datepicker(dpopts);
                    
                    calContainer.foreTeesMemberCalendar("reload");
                    return;
                }
                
                result.options = options;
                
                var selected_date = null;
                if(typeof(calContainer.attr('data-ftdefaultdate')) != "undefined" && calContainer.attr('data-ftdefaultdate').length){
                    selected_date = calContainer.attr('data-ftdefaultdate');
                } else if("selected_date" in pluginData){
                    selected_date = pluginData.selected_date;
                } else if("selected_date" in result){
                    selected_date = pluginData.result;
                }
                pluginData.selected_date = selected_date;
                
                var startDate = currentDate;
                var endDate = startDate;
                if("start_date" in result){
                    startDate = ftStringDateToDate(result.start_date);
                }
                if("end_date" in result){
                    endDate = ftStringDateToDate(result.end_date);
                }
                var day = currentDate.getDate();
                var year = currentDate.getFullYear();
                var month = currentDate.getMonth();
                var nextMonth = new Date (year, month + 1, 1);
                
                
                
                // Display loading indicator
                // Create and init the calendar, but disabled
                // Give our container a UID
                
                var blankCal = {
                    disabled:true, 
                    minDate: startDate,
                    maxDate: endDate, 
                    hideIfNoPrevNext: true
                }
                
                // Set default date if one was passed.
                if(selected_date != null){
                    calObj.data("viewingDate", ftStringDateToDate(selected_date));
                    //console.log(calObj.data("viewingDate").toString());
                    //calContainer.attr('data-ftdefaultdate','');
                    blankCal.minDate = ftStringDateToDate(selected_date);
                    blankCal.maxDate = ftStringDateToDate(selected_date);
                }

                if(typeof(calContainer.attr('data-ftmembercaluid')) == "undefined") {
                    calContainer.attr('data-ftmembercaluid','ftcal_'+pluginData.objectIndex);
                    pluginData.objectIndex ++;
                    //console.log(pluginData.objectIndex);
                    calObj.datepicker(blankCal);
                    calObj.data("defaultDate", calObj.data("viewingDate"));
                    calObj.datepicker("setDate",calObj.data("viewingDate"));
                } else {
                    calObj.data("defaultDate", calObj.data("viewingDate"));
                    calObj.datepicker("setDate",calObj.data("viewingDate"));
                    calObj.datepicker("disable");
                }

                if(calContainer.hasClass("primary")){
                    startDate = currentDate;
                    endDate = currentDate;
                }else if(calContainer.hasClass("secondary")){
                    startDate = nextMonth;
                    endDate = nextMonth;
                }
                
                pluginData.jsonData = result;
                calContainer.foreTeesMemberCalendar("show");
            });
        },
       
        reload: function(){
           
            $(this).foreTeesMemberCalendar('loadingIndicator');
            if(pluginData.jsonRunning == false){
                pluginData.jsonRunning = true;
                $.ajax({
                        
                    url: options.jsonSourceUrl,
                    context:$(this),
                    dataType: 'json',
                    success: function(data){
                        var calGroup = $(this).data("ft-calGroup");
                        $(this).foreTeesMemberCalendar('loadingIndicator','stop');
                        pluginData.jsonData = data;
                        pluginData.jsonRunning = false;
                        calGroup.foreTeesMemberCalendar('init');
                        
                    },
                    error: function(e){
                        pluginData.jsonRunning = false;
                        $(this).foreTeesMemberCalendar('loadingIndicator','stop');
                        $(this).foreTeesModal('calendarError');
                    }
                });
            }
            
        },
        
        loadingIndicator: function(option){
            var calContainer = $(this);
            if(calContainer.is("input")){
                return;
            }
            var calIndicator = calContainer.find(".foreTeesMemberCalendarIndicator").first();
            if(!calIndicator.length){
                calContainer.append('<div class="foreTeesMemberCalendarIndicator" style="position:absolute; top:0px; right:0px; left:0px; bottom:0px;"></div>');
                calIndicator = calContainer.find(".foreTeesMemberCalendarIndicator").first();
            }
            
            switch (option) {
                case "hide":
                    calIndicator.activity("stop");
                    calIndicator.remove();
                    break;
               
                default:
                case "show":
                    calIndicator.activity();
                    break;
            }
           
        },
       
        show: function(){
           
            $(this).each(function(index){
                var calContainer = $(this);
                //var calObj = calContainer.find(".foreTeesMemberCalendarObject").first();
                var calObj = calContainer.data("ft-calObject");
                var hidePrevNext = false;
                var calendarData = pluginData.jsonData;
                var startDate, lastDayOfMonth, nextMonth, endDate, endDateNext;
                
                // If we were passed the proper date values, get our start and end dates
                //console.log('Settings dates');
                var dateArray = new Object();
                if("date_map" in calendarData){
                    startDate = ftStringDateToDate(calendarData.start_date);
                    endDate = ftStringDateToDate(calendarData.end_date);
                    nextMonth = new Date(startDate.getYear(), startDate.getMonth()+1, 1);
                    lastDayOfMonth = new Date(startDate.getYear(), startDate.getMonth()+1, 0);
                }else{
                    if (calendarData.max == undefined) calendarData.max = 90;
                    //calendarData.max ++;
                    startDate = new Date (calendarData.cal.year, calendarData.cal.month, calendarData.cal.dayOfMonth);
                    lastDayOfMonth = new Date (calendarData.cal.year, calendarData.cal.month + 1, 0);
                    nextMonth = new Date (calendarData.cal.year, calendarData.cal.month + 1, 1);
                    endDate = new Date (calendarData.cal.year, calendarData.cal.month, calendarData.cal.dayOfMonth + (calendarData.max));
                    // Build an array of the dates we service, along with their associated code
                    
                    var keyDate = new Date();
                    for(var i=0;i<=(calendarData.max+1);i++) {
                        keyDate = new Date (calendarData.cal.year, calendarData.cal.month, calendarData.cal.dayOfMonth + i);
                        if(calendarData.IS_TLT){
                            dateArray[keyDate.format('mm/dd/yyyy')] = {
                                ft_tool_tip: options.toolTips.tlt, 
                                ft_code:'ft_code_0',
                                display:true
                            };
                        }else {
                            dateArray[keyDate.format('mm/dd/yyyy')] = {
                                ft_tool_tip: options.toolTips[calendarData.daysArray.days[i]], 
                                ft_code:'ft_code_'+calendarData.daysArray.days[i],
                                display:true
                            };
                        }
                        //calObj.data('dateArray', dateArray);
                        //calObj.data('test', ["test1","test2"]);

                    }
                }
                endDateNext = endDate;
                var endDateSingle = endDate;
                if(endDateNext < nextMonth && !("date_map" in calendarData)){
                    // If there are no active days on the next month, set start to the second day, and end to the first day
                    // to display the next month, but disable selection of all days
                    nextMonth = new Date (calendarData.cal.year, calendarData.cal.month + 1, 2);
                    endDateNext = new Date (calendarData.cal.year, calendarData.cal.month + 1, 1);
                }
                //var todaysDate = startDate;
                //calObj.data('todaysDate', startDate);
                if(calContainer.hasClass("primary")){
                    if(lastDayOfMonth < endDate){
                        endDate = lastDayOfMonth;
                    }
                    hidePrevNext = true;
                }else if(calContainer.hasClass("secondary")){
                    startDate = nextMonth;
                    endDate = endDateNext;
                }
                calObj.datepicker("setDate",calObj.data("defaultDate"));
                if(typeof(calObj.data("defaultDate")) != "undefined"){
                //console.log(typeof(calObj.data("defaultDate")));
                //console.log(calObj.data("defaultDate").toString('mm/dd/yyyy'));
                }
                var cd = calObj.data('ftCalData');
                var gotoCurrent = true;
                var dd = calObj.data("defaultDate");
                if(dd >= startDate && dd <= endDate){
                    calContainer.addClass("showDefaultDate")
                }
                if(!cd){
                    calObj.data('ftCalData',{
                        startDate: startDate,
                        endDate: endDate,
                        hidePrevNext : hidePrevNext,
                        endDateSingle : endDateSingle,
                        lastEndDate: endDate,
                        lastHidePrevNext : hidePrevNext
                    });
                    
                } else {
                    endDate = cd.lastEndDate;
                    hidePrevNext = cd.lastHidePrevNext;
                    gotoCurrent = false;
                }
                //console.log("sd:"+startDate.toString());
                //console.log("ed:"+endDate.toString());
                
                //var parent = calContainer.parent();
                function clearClasses(){
                    calObj.find("td>a.ui-state-active").removeClass("ui-state-active");
                    calObj.find("td>a.ui-state-hover").removeClass("ui-state-hover");
                }
                
                var dpopts = {
                    gotoCurrent: gotoCurrent,
                    defaultDate: dd,
                    disabled: false,
                    showOtherMonths:false,
                    minDate: startDate,
                    maxDate: endDate,

                    hideIfNoPrevNext: hidePrevNext,
                    beforeShowDay: function(date){
                        // Check if I have this date
                        //console.log(date);
                        var day_class = '';
                        var enable_day = true;
                        var tooltip = '';
                        var status = 0;
                        var date_string = date.format('mm/dd/yyyy');
                        
                        var calendar_map = pluginData.jsonData;

                        if("date_map" in calendar_map){
                            //console.log("searching date_map for:" + date_string);
                            
                            if(date_string in calendar_map.date_map){
                                //console.log("found date:" + date_string);
                                var selected_record = calendar_map.date_map[date_string];
                                status = selected_record.status;
                                if("tooltip" in selected_record){
                                    tooltip = selected_record.tooltip;
                                } else {
                                    var tooltip_object = ftReplaceKeyInString(calendar_map.tooltip_object, calendar_map);
                                    tooltip = ftReplaceKeyInString(tooltip_object[status], calendar_map);
                                }
                            //console.log("set tooltip:" + tooltip);
                            //console.log("set status:" + status);
                            }else if ("default_status" in calendar_map) {
                                status = calendar_map.default_status;
                            }
                            if("disabled_status_array" in calendar_map && $.inArray(status, calendar_map.disabled_status_array) != -1){
                                enable_day = false;
                            //console.log("disable day:" + status);
                            }
                            day_class = 'ft_code_' + status;
                        }else{
                            //var dateArray = $(this).data('dateArray');
                            if(typeof dateArray == "object" && typeof dateArray[date_string] == "object"){
                                var dtObj = dateArray[date_string];
                                if(dtObj.display){
                                    if(options.disableOnDayValue != null && dtObj.ft_code == options.disableOnDayValue){
                                        enable_day = false;
                                    }else{
                                        day_class = dtObj.ft_code;
                                        enable_day = true;
                                        tooltip = dtObj.ft_tool_tip;
                                    }
                                }else{
                                    enable_day = false;
                                }
                            }
                        }
                        if(typeof startDate != "undefined" && date_string == startDate.format('mm/dd/yyyy')){
                            day_class = 'ft_current_day ' + day_class;
                        }
                        return [enable_day, day_class, tooltip];
                    },
                    onSelect: options.onSelect,
                    
                    onChangeMonthYear: function(year,month,inst){
                        //var calContainer = $(this);
                        //var my_uid = calContainer.parent().attr("data-ftmembercaluid");
                        calContainer.data("viewingDate", new Date(year, month-1, 1)); 
                        
                        //calContainer.data("")
                        //console.log(calContainer.get(0).tagName);
                        // Unfortunately, datepicker doesn't have a renderCompleted callback,
                        // So we'll need to clear default selected day styles via setTimeout.
                        // Repeat it a few different times, just to make sure we get it
                        // after the calendar is rendered.
                        // 
                        //setTimeout('$(\'[data-ftmembercaluid="'+my_uid+'"]\').foreTeesMemberCalendar("clearClasses");',300);
                        //setTimeout('$(\'[data-ftmembercaluid="'+my_uid+'"]\').foreTeesMemberCalendar("clearClasses");',100);
                        //setTimeout('$(\'[data-ftmembercaluid="'+my_uid+'"]\').foreTeesMemberCalendar("clearClasses");',50);
                        //setTimeout('$(\'[data-ftmembercaluid="'+my_uid+'"]\').foreTeesMemberCalendar("clearClasses");',20);
                        //setTimeout('$(\'[data-ftmembercaluid="'+my_uid+'"]\').foreTeesMemberCalendar("clearClasses");',10);
                        
                        setTimeout(function(){clearClasses();},300);
                        setTimeout(function(){clearClasses();},100);
                        setTimeout(function(){clearClasses();},50);
                        setTimeout(function(){clearClasses();},20);
                        setTimeout(function(){clearClasses();},10);

                    }
                };
                
                if(calContainer.is('input')){
                        dpopts.showOn = "both";
                        dpopts.format = 'mm/dd/yyyy';
                        dpopts.buttonImage = "../assets/images/calendar_field.png";
                        dpopts.showButtonPanel= true;
                        dpopts.closeText = 'Close';
                        dpopts.currentText = '';
                }
                calObj.datepicker("option",dpopts);
                if(calContainer.is('input')){
                        calObj.datepicker("widget")
                            .addClass("calendar")
                            .addClass("aboveMenu");
                        calContainer.attr("readonly","true");
                }
                calObj.datepicker("setDate",calObj.data("defaultDate"));
                clearClasses();
                //calObj.foreTeesMemberCalendar("clearClasses");
                calContainer.foreTeesMemberCalendar('loadingIndicator','hide');
                calObj.datepicker("enable");
                $(window).trigger('ft-resize');
            });

        },
        /*
        // We use this to clear the active states of the calendar
        clearClasses: function(){
            // $(this).each(function(index){
            $(this).find("td>a.ui-state-active").removeClass("ui-state-active");
            $(this).find("td>a.ui-state-hover").removeClass("ui-state-hover");
        // });
        },
        */
        setOptions: function ( option ) {
            
            if(typeof option == "object"){
                //console.log("setting options");
                $.extend(true, options,option);
            }
            
        }
    };
    
    $.fn.foreTeesMemberCalendar = function ( method ){
        if (methods[method]) {
            return methods[ method ].apply (this, Array.prototype.slice.call( arguments, 1));
        } else if ( typeof method === 'object' || ! method) {
            return methods.init.apply( this, arguments );
        } else {
            $.error( 'Method ' + method + ' does not exist on jQuery.foreTeesMemberCalendar' );
        }     
    }

})(jQuery);
