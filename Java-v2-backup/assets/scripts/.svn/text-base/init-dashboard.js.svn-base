/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function() {
    
    var $dashboards = $(".mp_dashboard_selection");
    
    if($dashboards.length){
        // Create a dynamic CSS class for styling the font-family in graphs
        var ftt = $('body').getStyles(['font-family']), ftta = [], k;
        for(k in ftt){
            ftta.push(k+':'+ftt[k]);
        }
        $('head').append('<style> .ftd_report_title{'+ftta.join(';')+'}</style>');
    }
    
    function _getDateFrom($el){
        if($el.hasClass('hasDatePicker')){
            return $el.datepicker("getDate");
        } else {
            return ftcalendar.stringToDate($el.val());
        }
    }
    
    function _setDateTo($el, date){
        if($el.hasClass('hasDatePicker')){
            $el.datepicker("setDate", date);
        } else {
            $el.val(date.format('mm/dd/yyyy'));
        }
    }
    
    $dashboards.each(function(){
        
        var allActivities = 999,
            hideClass = 'ft_hide',
            todayClass = 'ftd_is_today',
            $dbs = $(this),
            $evntsCalCon = $('.ftd_smallCal'),
            $evntsLargeCalCon = $('.ftd_largeCal'),
            $dp = $dbs.find('input.ft_date_picker_field'),
            $sl = $dbs.find('select[name=activity_id]'),
            $dateArrowCon = $dbs.filter('.fta_has_date_arrows'),
            $monthArrowCon = $dbs.filter('.mp_select_by_month'),
            $dayArrowCon = $dbs.filter('.mp_select_by_day');
            
            if(!$dp.length){
                $dp = $dbs.find('input.ft_date_hidden_field');
            }
            
        //$dbs.after($evntsCalCon);
        
        var activityMap = {};
        
        var _timers = {
            timeout:null,
            interval:null
        }
        
        var $dayBanner = $('<div class="ftd_day_banner"></div>');
        
        $dbs.filter('.mp_show_day_banner').after($dayBanner);
        $dateArrowCon.append('<div class="fts_date_select fts_date_back"><div><span class="ui-icon ui-icon-circle-triangle-w"></span></div></div><div class="fts_date_select fts_date_forward"><div><span class="ui-icon ui-icon-circle-triangle-e"></span></div></div>');
        
        var $dayBackArrow = $dayArrowCon.find('.fts_date_back>div'), $monthBackArrow = $monthArrowCon.find('.fts_date_back>div');
        
        $sl.find('option').each(function(){
            var $el = $(this),
            this_activity_id = parseInt($el.val(),10),
            this_activity_name = $el.text();
            if(this_activity_id != allActivities){
                activityMap[this_activity_id] = this_activity_name;
            }
        });
        
        var setDayBanner = function(date){
            var today = ftapi.getClubDate(), isToday = false;
            if(ftcalendar.startOfDay(date).getTime() == ftcalendar.startOfDay(today).getTime()){
                isToday = true;
                $dayBanner.addClass(todayClass);
            } else {
                $dayBanner.removeClass(todayClass);
            }
            $dayBanner.html((isToday?'<span><b>Today:</b></span>':'')+' <span>'+date.format('dddd, mmmm dS, yyyy')+'</span>');
        }
        
        var _initActivityItems = function(date, activity_id, activity_name){
            
            setDayBanner(date);
            
            if(ftcalendar.startOfDay(date).getTime() <= ftcalendar.startOfDay().getTime()){
                $dayBackArrow.addClass("ftd_hide");
            } else {
                $dayBackArrow.removeClass("ftd_hide");
            }
            
            if(ftcalendar.firstDayOfMonth(date).getTime() <= ftcalendar.firstDayOfMonth().getTime()){
                $monthBackArrow.addClass("ftd_hide");
            } else {
                $monthBackArrow.removeClass("ftd_hide");
            }
            
            var activeItemsSelector = '', inactiveItemsSelector = '', active_activities = [],tmpa = [];
            if(activity_id == allActivities){
                for(k in activityMap){
                    tmpa.push('.ftd_activity_'+k); 
                    active_activities.push(k);
                }
                activeItemsSelector = tmpa.join(',');
            } else {
                for(k in activityMap){
                    tmpa.push('.ftd_activity_'+k); 
                    active_activities.push(k);
                }
                active_activities.push(activity_id);
                inactiveItemsSelector = tmpa.join(',');
                activeItemsSelector = '.ftd_activity_'+activity_id;
            }
            $(inactiveItemsSelector).addClass(hideClass);
            $(activeItemsSelector).removeClass(hideClass);
            
            // Init the calendar
            if($evntsCalCon.length){
                var $evntsCal = ftcalendar.buildCalendar(
                {
                    startDate:date,
                    mode:"list",
                    days:5,
                    activityId:activity_id,
                    clubCal:true,
                    captionPrefix: activity_name+' Events from ',
                    forcedDayUrl: ftUpdateQueryString('activity_id',activity_id,ftUpdateQueryString('date', 'replace_with_date')),
                    forcedDayUrlTitle: 'Show dashboard for this day'
                }, $evntsCalCon
                );
                $evntsCalCon.replaceWith($evntsCal);
                $evntsCalCon = $evntsCal;
            }
            
            if($evntsLargeCalCon.length){
                var $evntsLargeCal = ftcalendar.buildCalendar(
                {
                    startDate:date,
                    //mode:"month", // default is month
                    //days:5, // month doesn't use days.
                    activityId:activity_id,
                    clubCal:true,
                    captionPrefix: activity_name+' Events for ',
                    forcedDayUrl: 'Manager_dashboard?activity_id='+activity_id+'&date=' //,
                    //forcedDayUrlTitle: 'Show dashboard for this day'
                }, $evntsLargeCalCon
                );
                $evntsLargeCalCon.replaceWith($evntsLargeCal);
                $evntsLargeCalCon = $evntsLargeCal;
            }
            
            
            // Init the reports
            var $report_containers = $('.ftd_report:not(.'+hideClass+')');
            $report_containers.each(function(){
                var $el = $(this), json = $el.attr("data-ftreport"), auto_load_id = parseInt($el.attr("data-ftautoload"),10) || 0, report;
                $el.removeAttr("data-ftautoload"); // don't auto load on refresh.
                try {
                    report = JSON.parse(json);
                } catch(err){
                    //console.log("Error parsing JSON:");
                    //console.log(json);
                }
                if(report){
                    //console.log("Doing:"+report.type);
                    ftreport[report.type]($el, report, {date:date, selected_activity_id:activity_id, auto_load_id:auto_load_id});
                }
            });
        }
        
        //var initial_date = $dp.datepicker("getDate"), initial_activity_id = parseInt($sl.val(),10);
        //var initial_state = {date:initial_date,activity_id:initial_activity_id};
        
        $monthBackArrow.click(function(){
            _setTimers();
            var date = _getDateFrom($dp), new_date = new Date(date.getFullYear(), date.getMonth()-1, 1);
            _setDateTo($dp,new_date);
            $dp.change();
        });
        $monthArrowCon.find('.fts_date_forward>div').click(function(){
            _setTimers();
            var date = _getDateFrom($dp), new_date = new Date(date.getFullYear(), date.getMonth()+1, 1);
            _setDateTo($dp,new_date);
            $dp.change();
        });
        
        $dayBackArrow.click(function(){
            _setTimers();
            var date = _getDateFrom($dp), new_date = new Date(date.getFullYear(), date.getMonth(), date.getDate()-1);
            _setDateTo($dp,new_date);
            $dp.change();
        });
        $dayArrowCon.find('.fts_date_forward>div').click(function(){
            _setTimers();
            var date = _getDateFrom($dp), new_date = new Date(date.getFullYear(), date.getMonth(), date.getDate()+1);
            _setDateTo($dp,new_date);
            $dp.change();
        });
        
        //if(history.pushState){
            //history.pushState(initial_state,document.title,ftUpdateQueryString("date", initial_date.format('yyyy-mm-dd'),ftUpdateQueryString("activity_id", initial_activity_id)));
        //}
        $dp.on("change", function(e){
            _setTimers();
            var $el = $(this), date = _getDateFrom($el), activity_id = parseInt($sl.val(),10);
            //var state = {date:date,activity_id:activity_id};
            //if(history.pushState){
                //history.pushState(state,document.title,ftUpdateQueryString("date", date.format('yyyy-mm-dd'),ftUpdateQueryString("activity_id", activity_id)));
            //}
            //console.log($el.datepicker("getDate").format());
            _initActivityItems(date, activity_id, $sl.find('option:selected').text());
        });
        
        $sl.on("change", function(e){
            _setTimers();
            var $el = $(this), date = _getDateFrom($dp), activity_id = parseInt($el.val(),10);
            //var state = {date:date,activity_id:activity_id};
            //if(history.pushState){
                //history.pushState(state,document.title,ftUpdateQueryString("date", date.format('yyyy-mm-dd'),ftUpdateQueryString("activity_id", activity_id)));
            //}
            //$sl = $el;
            _initActivityItems(date, activity_id, $el.find('option:selected').text());
        });
        /*
        $(window).on("popstate",function(event){
            console.log('popstate fired!');
            console.log(event);
            console.log(event.originalEvent.state);
            console.log(history.state);
            var state = event.originalEvent.state;
            if(state.date){
                $sl.val(state.activity_id);
                $dp.val(state.date.format('mm/dd/yyyy'));
                _initActivityItems(state.date, state.activity_id, $sl.find('option:selected').text());
            }
        });
        */
       
       function _refreshData(){
           if(!$(".ui-dialog").is(":visible")){
               // If no dialogs are open, then refresh the stats
               //console.log('loading data');
               _initActivityItems(_getDateFrom($dp), parseInt($sl.val(),10), $sl.find('option:selected').text());
           } else {
               // Dialog was open.  Try again in 5 seconds.
               //console.log('waiting...');
               if(_timers.timeout){
                   clearTimeout(_timers.timeout);
               }
               if(_timers.interval){
                   clearInterval(_timers.interval);
               }
               _timers.timeout = setTimeout(function(){_setTimers()},1000);
           }
       }
       
       function _setTimers(){
           if(_timers.timeout){
               clearTimeout(_timers.timeout);
           }
           if(_timers.interval){
               clearInterval(_timers.interval);
           }
           _timers.interval = setInterval(function(){
               _refreshData()
           //},(1000*60)*5); // Reresh every five minutes
           },(1000*30)); // Reresh every five minutes
       }
       
       
       _refreshData(); // force initial refresh of data.
       _setTimers();
       
       
    });
});