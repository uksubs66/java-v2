/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


var ftcalendar = (function(){
    
    var _addDays = function(date, days){
        return new Date(date.getFullYear(),date.getMonth(),date.getDate()+days);
    }
    
    var _monthsBetween = function(date1, date2) {
        var year1=date1.getFullYear(),
            year2=date2.getFullYear(),
            month1=date1.getMonth(),
            month2=date2.getMonth();
        if(month1===0){ //Have to take into account
            month1++;
            month2++;
        }
        return Math.abs((year2 - year1) * 12 + (month2 - month1)) + 1;
    }
    
    var _yearsBetween = function(date1, date2) {
        var mb = _monthsBetween(date1, date2);
        return Math.floor(mb / 12);
    }
    
    var _daysBetween = function(date1, date2) {
        
        return Math.round(Math.abs(_startOfDay(date1).getTime() - _startOfDay(date2)) / (24 * 60 * 80 * 1000));
        
    }
    
    var _startOfDay = function(date){
        
        if(!date){
            date = new Date();
        }
        return new Date(date.getFullYear(), date.getMonth(), date.getDate());
        
    }
    
    var _isSameDay = function(date1, date2){
        
        return (date1.getFullYear() == date2.getFullYear() && date1.getMonth() == date2.getMonth() && date1.getDate() == date2.getDate());
        
    }
    
    var _lastDayOfMonth = function(date, addMonths){
        
        if(!addMonths){
            addMonths = 0;
        }
        addMonths ++;
        
        return new Date(date.getFullYear(), date.getMonth()+addMonths, 0);
        
    }
    
    var _firstDayOfWeek = function(date){

        return new Date(date.getFullYear(), date.getMonth(), date.getDate() - date.getDay());
        
    }
    
    var _lastDayOfWeek = function(date){
        
        return new Date(date.getFullYear(), date.getMonth(), date.getDate() + (6 - date.getDay()));
        
    }
    
    var _firstDayOfMonth = function(date){
        if(!date){
            date = new Date();
        }
        return new Date(date.getFullYear(), date.getMonth(), 1);
        
    }
    
    var _daysInMonth = function(date){
        
        return _lastDayOfMonth(date).getDate();
        
    }
    
    var _stringToDate = function(strDate){
        
        if(!strDate){
            return new Date();
        }
        
        if(Object.prototype.toString.call(strDate) === '[object Date]'){
            // already a Date object
            return strDate;
            
        }
        
        var match;
        if(match = strDate.match(/^([0-9]{4})[-\/]([0-9]{1,2})[-\/]([0-9]{1,2})$/)){
            // YYYY-MM-DD (or YYYY-MM-DD)
            return _stringsToDate(match[1],match[2],match[3]);
        } else if(match = strDate.match(/^([0-9]{1,2})[-\/]([0-9]{1,2})[-\/]([0-9]{4})$/)){
            // MM/DD/YYYY (or MM-DD-YYYY)
            return _stringsToDate(match[3],match[1],match[2]);
        } else if(match = strDate.match(/^([0-9]{4})[-\/]([0-9]{1,2})[-\/]([0-9]{1,2}) ([0-9]{1,2}):([0-9]{2})$/)){
            // YYYY-MM-DD HH:MM
            return _stringsToDate(match[1],match[2],match[3],match[4],match[5]);
        } else if(match = strDate.match(/^([0-9]{4})[-\/]([0-9]{1,2})[-\/]([0-9]{1,2}) ([0-9]{1,2}):([0-9]{2}):([0-9]{2})$/)){
            // YYYY-MM-DD HH:MM:SS
            return _stringsToDate(match[1],match[2],match[3],match[4],match[5],match[6]);
        } else if(match = strDate.match(/^([0-9]{4})[-\/]([0-9]{1,2})[-\/]([0-9]{1,2}) ([0-9]{1,2}):([0-9]{2}):([0-9]{2}):([0-9]{1,3})$/)){
            // YYYY-MM-DD HH:MM:SS:MS
            return _stringsToDate(match[1],match[2],match[3],match[4],match[5],match[6],match[7]);
        } else if(match = strDate.match(/^([0-9]{4})[-\/]([0-9]{1,2})[-\/]([0-9]{1,2}) ([0-9]{1,2}):([0-9]{2}):([0-9]{2}).([0-9]{1,3})$/)){
            // YYYY-MM-DD HH:MM:SS.MS
            return _stringsToDate(match[1],match[2],match[3],match[4],match[5],match[6],match[7]);
        } else {
            return new Date();
        }
        
    }
    
    var _stringsToDate = function(year,month,day,hour,min,sec,msec){
        hour = hour?hour:0;
        min = min?min:0;
        sec = sec?sec:0;
        msec = msec?msec:0;
        return new Date(parseInt(year, 10), parseInt(month, 10)-1, parseInt(day, 10), parseInt(hour, 10), parseInt(min, 10), parseInt(sec, 10), parseInt(msec, 10));
    }
    
    var _buildCalendar = function(opt, $oldCalendar){
        
        // Set up  some default options based on selected mode (month, week, or list of days)
        
        if(opt.days>7){
            opt.days = 7;
        }
        
        if(opt.days<1){
            opt.days = 1;
        }
        
        if(!$oldCalendar){
            $oldCalendar = $('<div></div>');
        }
        
        var today = ftapi.getClubDate(),  // Get current date from club (last date returned from API -- does not generate an API call)
            isList = false, 
            isWeek = false,
            osd = opt.startDate, 
            oed = opt.endDate, 
            om = opt.months, 
            od = opt.days,
            defaultMonths = om?om:1,
            defaultDays = od?od:7,
            defaultStartDate = osd?osd:today,
            selectedStartDate = defaultStartDate,
            defaultEndDate,
            defaultCaptionHtml;
            
        //console.log("defaultStartDate:" + defaultStartDate.format());
            
        switch(opt.mode){
            case "list": // List from date to date.
                isList = true;
                if(oed  && !od){
                    defaultDays = _daysBetween(defaultStartDate, oed);
                    if(defaultDays > 7){
                        defaultDays = 7;
                    }
                    if(defaultDays < 1){
                        defaultDays = 1;
                    }
                }
                defaultEndDate = _addDays(defaultStartDate, defaultDays-1);
                defaultCaptionHtml = '<b>'
                    +defaultStartDate.format('mmm dS')+'</b> &nbsp;to&nbsp; <b>'
                    +defaultEndDate.format('mmm dS')+'</b>';
                break;

            case "week": // Week of a given date
                isWeek = true;
                defaultStartDate = _firstDayOfWeek(defaultStartDate);
                defaultEndDate = _lastDayOfWeek(defaultStartDate);
                defaultCaptionHtml = '<b>Week of '+selectedStartDate.format('mmmm dS, yyyy')+'</b>';
                break;

            default: // Month of a given date
                if(oed && !om){
                    defaultMonths = _monthsBetween(defaultStartDate, oed);
                }
                defaultStartDate = _firstDayOfMonth(defaultStartDate);
                defaultEndDate = _lastDayOfMonth(defaultStartDate, defaultMonths-1);
                break;
        }
        
        opt.startDate = defaultStartDate;
        opt.endDate = defaultEndDate;
        opt.days = defaultDays;
        opt.months = defaultMonths;
        
        //console.log("defaultStartDate:" + defaultStartDate.format());
        //console.log("defaultEndDate:" + defaultEndDate.format());
        //console.log("defaultDays:" + defaultDays);
        //console.log("defaultMonths:" + defaultMonths);

        // Create a defaults options object 
        var defaultOptions = {
            className:'',
            rowHtml:'',
            mode:'month', // month, week, list
            days:defaultDays, // in list, number of days of data to show
            months:defaultMonths, // Number of calendars to show. // not used in list mode
            startDay:1, // 1=Sunday.  Not used for list mode
            showDayNumber: true,
            headLong:'dddd', // for wider screen devices
            headShort:'ddd', // for amaller screen devices
            dayFormat:'d',
            showCaption:true,
            captionFormat:'mmmm yyyy',
            captionHtml:defaultCaptionHtml,
            captionPrefix:'',
            captionSuffix:'',
            forcedDayUrl:'',
            forcedDayUrlTitle:'',
            forcedDayUrlFormat:'yyyy-mm-dd',
            startDate:defaultStartDate,
            endDate:defaultEndDate,
            blockPast:true,
            //clubCal:null,
            memberId:null,
            activityId:null
            
        }

        // Merge it with the options passed to thos function
        var options = $.extend(true, {}, defaultOptions, opt), 
            startDate = options.startDate, 
            endDate = options.endDate,
            idate;
            
        // Set dates based on mode
        if(isList){
            options.months = 1;
            options.startDay = 1;
            idate = startDate;
        } else if(isWeek){
            options.months = 1;
            idate = startDate;
        } else {
            options.months = _monthsBetween(startDate, endDate);
            idate = _firstDayOfMonth(startDate);
        }
        
        // Set oft used variables for peformance and javascript compression reasons:
        var days = options.days, 
            startDay = options.startDay, 
            months = options.months, 
            tables = '', 
            rows = '', 
            cells = '', 
            day = startDay, 
            month = 0, 
            new_table = true, 
            tableDate = idate, 
            imonth=idate.getMonth(),
            $calendar,
            oldOptions = $oldCalendar.data('ft_calendar_options'),
            oldData = $oldCalendar.data('ft_calendar_data');
       
       if(ftapi.compareObjects(options, oldOptions)){
           // Our options match the last options used.
           // Re-use the old calendar object
           //console.log('Re-using old calendar');
           $calendar = $oldCalendar;
       } else {
           // Build a new calendar object
           //console.log('Building new calendar');
           while(idate.getTime() <= endDate.getTime() && month < months){

                //console.log("idate:" + idate.getTime());
                //console.log("edate:" + endDate.getTime());
                //console.log("month:" + month);

                if(!isList && new_table){
                    var offset = (idate.getDay()+1) - day;
                    if(offset < 0){
                        // Increment to first day we'll show
                        idate = _addDays(idate, Math.abs(offset));
                        tableDate = idate;
                    } else if(offset > 0) {
                        // Insert blank cells 'till the first available day
                        cells += _getEmptyCells(offset);
                        day += offset;
                    }
                }
                cells += _getDayCell(idate, options);

                day ++;
                if(day > days){
                    new_table = false;
                    day = startDay;
                    rows += _getRow(cells, idate, options);
                    cells = '';
                }
                idate = _addDays(idate, 1); // Increment to next day
                if(idate.getMonth() !== imonth && !(isList || isWeek) || (rows.length && (isList || isWeek) )){
                    // End of table, rotate calendar
                    if(cells.length && day < days){
                        cells += _getEmptyCells(days-(day-1));
                    }
                    if(cells.length){
                        rows += _getRow(cells, idate, options);
                        cells = '';
                    }
                    tables += _getTable(rows, tableDate, options);
                    tableDate = idate;
                    rows = '';
                    new_table = true;
                    imonth=idate.getMonth();
                    month ++;
                }
            }

            $calendar = $('<div '+_getTableClass(options)+'>'+tables+'</div>');
            $calendar.addClass("ftLoading");
       }
       
       
        
        $calendar.data('ft_calendar_options',options); // store our options for later comparison

        // Fill it with data
        
        var __loadData = function(data){
            $calendar.removeClass("ftLoading");
            if(ftapi.compareObjects(data, oldData)){
                // Data the same.  Do nothing?
                //console.log("No change in data.  Don't update calendar.");
            } else {
                // Data has changed.  Update calendar
                //console.log("New data.  Updating calendar.");
                _setCalendarData($calendar, data, options);
                ftActivateElements($calendar);
                _activate($calendar.find('table')); 
            }
            $calendar.data('ft_calendar_data', data);
        }

        var ajaxCommand = {
            command:'clubCalendar',
            date_start:options.startDate.format('yyyy-mm-dd'),
            date_end:options.endDate.format('yyyy-mm-dd'),
            activity_id:options.activityId,
            member_id:options.memberId,
            success:__loadData
        }
        
        if(options.clubCal){
            ajaxCommand.clubCal = true;
        }
        
        //if(options.data){
        //    __loadData(options.data);
        //} else {
            
            ftapi.get(ajaxCommand,{ccq:true});
        //}
        
        return $calendar;
        
    }
    
    var _setCalendarData = function($calendar, data, options){
        var i, e, entries, calEntry, entriesHtml, $calCell;
        for(i in data){
            calEntry = data[i];
            entries = calEntry.entries;
            entriesHtml = '';
            for(e in entries){
                entriesHtml += _getEntryHtml(entries[e], options);
            }
            $calCell = $calendar.find('td.ftcal_'+calEntry.date);
            $calCell.find('.item_container').remove(); // Remove any old entries
            $calCell.find('.day_container .day').after(entriesHtml); // Add new
        }
    }
    
    var _getEntryHtml = function(data, options){
        var entryData = data.data, result = '', time = data.time, label = data.label;
        if(entryData){
            // Linkable
            var linkData = entryData.data;
            if(linkData){
                linkData = $.extend({},entryData.data); // Copy so we don't modify original data
                linkData.url = entryData.url;
                linkData.base_url = entryData.base_url;
            }
            if(options.showIcal && data.registered){
            // data object doesn't seem to contain enough data to do this yet'
            }
            result += '<a href="#" class="'+data.sub_category+'_button '+data.css_class+'" data-ftjson="'+ftEscape(JSON.stringify(linkData))+'">'
                + (data.time?'<b>' + data.time + '</b>: ':'') + data.label + '</a>';
        } else if(time && label) {
            // Non Linkable
            result += '<div class="'+data.css_class+'">'
                + '<b>' + time + '</b>: ' + label + '</div>';
        }
        return '<div class="item_container">'+result+'</div>';
    }
    
    var _getDayCell = function(date, options, cls){
        var oldDay = options.blockPast && _startOfDay(date) < _startOfDay();
        return '<td class="'+(oldDay?'old ':'')+'ftcal_'+date.format('yyyy-mm-dd')+(cls?' '+cls:'')+'"><div class="day_wrapper'+(oldDay?'_empty':'')+'"><div class="day_container">'
            +(options.showDayNumber?_getDayLink(date, options):'')+(oldDay?'':'<div class="item_container"></div>')+'</div></div></td>';
    }
    
    var _getDayLink = function(date, options, title){
        var forcedDayUrl = options.forcedDayUrl, dayUrl, oldDay = options.blockPast && _startOfDay(date) < _startOfDay();
        if(!oldDay && forcedDayUrl){
            var urlDate = encodeURIComponent(date.format(options.forcedDayUrlFormat));
            if(forcedDayUrl.match(/replace_with_date/)){
                dayUrl = forcedDayUrl.replace(/replace_with_date/,urlDate);
            } else {
                dayUrl = forcedDayUrl + urlDate;
            }
            title = options.forcedDayUrlTitle;
        }
        if(dayUrl){
            return '<a class="day" href="'+dayUrl+'" '+(title?' title="'+title+'"':'')+'>'+date.format(options.dayFormat)+'</a>';
        } else {
            return '<div class="day">'+date.format(options.dayFormat)+'</div>';
        }
    }
    
    var _getRow = function(rowHtml, date, options, cls){
        return '<tr'+(cls?' class="'+cls+'"':'')+'>'+rowHtml+'</tr>';
    }
    
    var _getTable = function(rowHtml, date, options, cls){
        
        var sd = options.startDay, clsTag = _getTableClass(options, cls), captionHtml = options.captionHtml, headHtml = '';
        if(!captionHtml){
            captionHtml = '<b>'+date.format(options.captionFormat)+'</b>';
        }
        var headDate = options.mode=="list"?date:_addDays(_firstDayOfWeek(date),sd - 1);
        for(var i = sd; i <= options.days; i++ ){
            headHtml += '<th><b><span>'+headDate.format(options.headLong)+'</span><span>'+headDate.format(options.headShort)+'</span></b></th>';
            headDate = _addDays(headDate, 1); // Increment to next day
        }
        
        return '<table '+clsTag+'>'+(options.showCaption?'<caption>'+options.captionPrefix+captionHtml+options.captionSuffix+'</caption>':'')+'<thead><tr>'+headHtml+'</tr></thead><tbody>'+rowHtml+'</tbody></table>';
        
    }
    
    var _getTableClass = function(options, cls){
        return 'class="list_calendar ftcal_dynamic '+options.className+'"';
    }
    
    var _getEmptyCells = function(count){
        var result = '';
        for(var i = 0; i < count; i++){
            result += '<td class="empty">&nbsp;</td>';
        }
        return result;
    }
    
    var _activate = function($cal){

        if(!$cal.length){
            return;
        }
        var maxHeight = $cal.find('.day_wrapper').height(), $body = $('body'), events, $outmask = $('#ft_cal_outmask');
        if(!$outmask.length){
            $outmask = $('<div id="ft_cal_outmask"></div>');
            $outmask.addClass('ft_hide');
            $body.append($outmask);
        }
        if(window.navigator.pointerEnabled || window.navigator.msPointerEnabled){
            events = "MSPointerDown MSPointerOver pointerdown pointerover touchend focusin click";
        } else {
            events = "touchend focusin mouseover click";
        }
        if($cal.is('.processed')){
            //console.log('reprocess');
            var $proccessed = $cal.find('.day_wrapper.overSized');
            $proccessed.filter('.active').trigger('ftCalClose');
            $proccessed.removeClass('overSized').removeClass('active').find('.more_mask').remove();
            $cal.removeClass('processed');
        } else {
            $cal.find('.day_wrapper').bind('ftCalClose',function(e){
                var $parent = $(this), $target = $parent.find('.day_container');
                $parent.removeClass('active').removeClass('touched');
                $target.stop().css('width','').css('height','').css('left','').css('top','');
                if(!$cal.find('.active').length){
                    $cal.addClass('waiting');
                }
            //document.removeEventListener('click',ignoreClick,true);
            });
            $cal.find('td, thead tr, caption, #ft_cal_outmask').on(events,function(e){
                $cal.find('.active').not($(this).find('.day_wrapper')).trigger('ftCalClose');
            });
            $outmask.on(events,function(e){
                $outmask.addClass('ft_hide');
                e.preventDefault();
                e.stopPropagation();
                $cal.find('.active').not($(this).find('.day_wrapper')).trigger('ftCalClose');
            });
            
            $(window).on('ft_widthchange', function(){
                //console.log("cal_width_change!");
                _activate($cal);
            });
        }
        $cal.addClass('waiting');
        var $overSized = $cal.find(".day_container").filter(function(i){
            var $el = $(this), size = {
                w:$el[0].scrollWidth,
                h:$el[0].scrollHeight
            };
            $el[0].ftSize = size;
            return(size.w > $el.width() || size.h > maxHeight);
        });
        var $parents = $overSized.closest('.day_wrapper');
        $parents.addClass('overSized').append('<a class="more_mask"><b>...more</b></a>');
        var $masks = $parents.find('.more_mask');
        $cal.addClass('processed');
        $masks.on(events,function(e){
            var $parent = $(this).closest('.day_wrapper');
            //var parentTd = $(this).closest('td');
            e.preventDefault();
            e.stopPropagation();
            //e.stopImmediatePropagation();
            if($parent.is('.active')){
                //console.log("next:"+e.type);
                return;
            }
            $outmask.removeClass('ft_hide');
        
            //document.addEventListener('click',ignoreClick,true);
        
            //console.log("first:"+e.type);
        
        
            //alert("pt:"+e.originalEvent.pointerType);
            if(e.type == "touchstart" || e.originalEvent.pointerType == 2 || e.originalEvent.pointerType == "touch" || e.originalEvent.pointerType == "mouse"){ // is the an IE touch event?
                $parent.addClass('touched'); // add a class to help keep the future emulated click event from bubbling 
            //console.log("type:"+e.originalEvent.pointerType);
            }
            $parent.addClass('active');
            var $target = $parent.find('.day_container'), size = $target[0].ftSize;
            if(size.h < $parent.height()){
                size.h = $parent.height();
            }
            $target.width($parent.width()).height($parent.height()).css('left','').css('top','');
            
        
            $cal.find('.active').not($parent).trigger('ftCalClose');
            $cal.removeClass('waiting');
            var windowWidth = $body.width(), 
                windowHeight = $body.height(),
                offset = $target.offset(),
                offsetLeft = $target.position().left,
                offsetTop = $target.position().top;
            if(offset.left + size.w + 10 > (windowWidth+$(window).scrollLeft())){
                offsetLeft += (windowWidth+$(window).scrollLeft()) - (offset.left + size.w + 10);
            }
            if(offset.top + size.h + 10 > (windowHeight+$(window).scrollTop())){
                offsetTop += (windowHeight+$(window).scrollTop()) - (offset.top + size.h + 10);
            }
        
            $target.animate(
            {
                left: offsetLeft,
                top: offsetTop,
                width: size.w,
                height: size.h,
                avoidCSSTransitions:true
            },

            {
                duration: 100,
                complete: function(){
                    $parent.removeClass('touched');
                //document.removeEventListener('click',ignoreClick,true);
                }
            }
            );
        });
    };
    
    // Export public methods
    return {
        activate:_activate, 
        buildCalendar:_buildCalendar, 
        stringToDate:_stringToDate,
        addDays:_addDays,
        yearsBetween:_yearsBetween,
        daysBetween:_daysBetween,
        startOfDay:_startOfDay,
        firstDayOfMonth:_firstDayOfMonth
    };
    
})();