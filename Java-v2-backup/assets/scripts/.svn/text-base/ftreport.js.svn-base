/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var ftreport = (function(){
    
    var _getTitleStyles = function(options){
        
        var titleFontStyles = options.titleStyles, $titleFontFrom = $(options.titleFontFrom);
        
        if($titleFontFrom.length){
            titleFontStyles = $.extend(true, {}, $titleFontFrom.getStyles(['font-family','font-size','font-weight']), titleFontStyles);
        }
        
        return titleFontStyles;
    }
    
    var _getOrAdd = function(selectorClass, $container, content){
        var classes = selectorClass.trim().split(/ +/);
        var $item = $container.find('.'+classes.join('.'));
        if(!$item.length){
            $item = $('<div class="'+selectorClass+'"></div>');
            $container.append($item);
        }
        if(content){
            $item.html(content);
        }
        return $item;
    }
    
    var _vipsWithReservations = function($container, opt, opt2){
        
        var today = ftapi.getClubDate();
        
        var defaultOptions = {
            date:today,
            days:1,
            selected_activity_id:0,
            title:'VIPs with Reservations',
            titleFontFrom:null, // jquery object where you would like to copy fonts from
            titleStyles:{}
        }

        var options = $.extend(true, {}, defaultOptions, opt, opt2), 
            tfs = _getTitleStyles(options), 
            oldOptions = $container.data('ft_report_options');
        
        // Make sure it's a valid date object
        options.date = ftcalendar.stringToDate(options.date);
        
        var title = options.title, 
            days = options.days, 
            startDate = options.date, 
            endDate = ftcalendar.addDays(startDate,days-1), 
            $caption, 
            $report;
        
        if(days > 1){
            $caption = _getOrAdd('ftd_caption', $container, '<span><b>'+title + '</b></span> <span>'+startDate.format('mmm dS')+' - '+endDate.format('mmm dS')+'</span>');
        } else if(startDate.format('yyyy-mm-dd') == today.format('yyyy-mm-dd')) {
            // Today
            $caption = _getOrAdd('ftd_caption', $container, '<span><b>'+title + ' Today</b></span>');
        } else {
            $caption = _getOrAdd('ftd_caption', $container, '<span><b>'+title + '</b></span> <span>'+startDate.format('mmm dS')+'</span>');
        }
        
        if(!ftapi.compareObjects(options,oldOptions)){
            // Options have changed since last update.  Clear report
            $report = _getOrAdd('ftd_report_list ftd_constrain', $container, " ");
            $report.empty();
            $container.data('ft_report_data',null);
        } else {
            //console.log("VIP options have not changed");
        }
        
        $container.data('ft_report_options',options);
        
        var my_time = new Date().getTime();
        $container.data('ft_report_time',my_time);
        
        var ajaxCommand = {
            command:'clubVipMembersWithReservations',
            date_start:startDate.format('yyyy-mm-dd'),
            date_end:endDate.format('yyyy-mm-dd'),
            activity_id:options.selected_activity_id,
            success:function(data){
                if(my_time < $container.data('ft_report_time')){
                    // Another refresh happened for this report before I finished.  Abort!
                    //console.log("I've been outmoded!");
                } else {
                    var i, item, oldData = $container.data('ft_report_data');
                    $container.data('ft_report_data',data);
                    if(!ftapi.compareObjects(data,oldData)){
                        // Data has changed.  Update report 
                        $report = _getOrAdd('ftd_report_list ftd_constrain', $container, " ");
                        $report.empty();

                        if(data.length){

                                for(i in data){
                                    item = data[i];
                                    if(item.id){
                                        $report.append('<div data-ftitem="'+i+'" class="ftd_report_item ftd_clickable_item"><span>'+item.full_name+'</span><span class="ftd_list-small"></span></div>');
                                    }
                                }

                                $report.find('.ftd_report_item').click(function(){

                                    var $el = $(this), i = parseInt($el.attr('data-ftitem'),10), member = data[i];

                                    $('body').foreTeesModal("pleaseWait");

                                    var ajaxCommand = {
                                        command:'clubCalendar',
                                        date_start:startDate.format('yyyy-mm-dd'),
                                        date_end:endDate.format('yyyy-mm-dd'),
                                        activity_id:options.selected_activity_id,
                                        member_id:member.id,
                                        success:function(data){

                                            //console.log(data);
                                            $('body').foreTeesModal("pleaseWait","close");
                                            var regs = data[0], listhtml = '';
                                            if(regs && regs.entries){
                                                var entries = regs.entries;
                                                if(entries.length){
                                                    for(var i2 = 0; i2 < entries.length; i2++){
                                                        var entry = entries[i2];
                                                        if(entry.registered){
                                                            //console.log(entry.time + ': ' + entry.label);
                                                            listhtml += '<li><span><b>'+entry.time+':</b></span> <span>'+entry.label+'</span></li>';
                                                        }
                                                    }
                                                } else {
                                            //console.log('no entries');
                                            }
                                            } else {
                                            //console.log('no calendar data');
                                            }
                                            if(!listhtml){
                                                listhtml = 'No registrations found.';
                                            } else {
                                                listhtml = '<div>Reservations for: '+ftcalendar.stringToDate(regs.date).format('mmmm dS')+'</div><ul>'+listhtml+'</ul>'
                                            }
                                            $report.foreTeesModal("alertNotice", {
                                                title:member.full_name + ' Reservations',
                                                alertMode: false,
                                                message: listhtml
                                            });
                                        }
                                    }

                                    ftapi.get(ajaxCommand,{
                                        beforeError:function(){
                                            $('body').foreTeesModal("pleaseWait","close")
                                            }
                                        });
                                });

                        } else {
                            $report.append('<div class="ftd_report_item"><span>No VIPs with reservations.</span></div>');
                        }
                    } else {
                        //console.log("VIP Data has not changed");
                    }
                }
            }
        }
        if(options.clubCal){
            ajaxCommand.clubCal = true;
        }
        
        ftapi.get(ajaxCommand,{ccq:true});
        
    }
    
    var _upcommingBirthdays = function($container, opt, opt2){
        
        var today = ftapi.getClubDate();
        
        var defaultOptions = {
            date:today,
            days:7,
            title:'Upcomming Birthdays',
            titleFontFrom:null, // jquery object where you would like to copy fonts from
            titleStyles:{}
        }

        var options = $.extend(true, {}, defaultOptions, opt, opt2), 
            tfs = _getTitleStyles(options), 
            oldOptions = $container.data('ft_report_options'),
            $report;
        
        // Make sure it's a valid date object
        options.date = ftcalendar.stringToDate(options.date);
        
        var startDate = options.date, 
            endDate = ftcalendar.addDays(startDate,options.days), 
            $caption = _getOrAdd('ftd_caption', 
            $container, '<span><b>'+options.title + '</b></span> <span>'+startDate.format('mmm dS')+' - '+endDate.format('mmm dS')+'</span>');
            
        if(!ftapi.compareObjects(options,oldOptions)){
            // Options have changed since last update.  Clear report
            $report = _getOrAdd('ftd_report_list ftd_constrain', $container, " ");
            $report.empty();
            $container.data('ft_report_data',null);
        } else {
            //console.log("Birthday options have not changed");
        }
        
        $container.data('ft_report_options',options);
        
        var my_time = new Date().getTime();
        $container.data('ft_report_time',my_time);
        
        var ajaxCommand = {
            command:'clubMembersByBirthDate',
            date_start:startDate.format('yyyy-mm-dd'),
            date_end:endDate.format('yyyy-mm-dd'),
            success:function(data){
                if(my_time < $container.data('ft_report_time')){
                    // Another refresh happened for this report before I finished.  Abort!
                    //console.log("I've been outmoded!");
                } else {
                    var oldData = $container.data('ft_report_data');
                    $container.data('ft_report_data',data);
                    if(!ftapi.compareObjects(data,oldData)){
                        // Data has changed.  Update report 
                        $report = _getOrAdd('ftd_report_list ftd_constrain', $container, " ");
                        $report.empty();
                        var i, birthdate, age, birthMonth, birthDay, bdYear, bdText, daysUntil, bdDate, item, today = ftapi.getClubDate(), thisMonth = today.getMonth(), thisYear = today.getFullYear(), thisDay = today.getDate() ;
                        if(data.length){
                            for(i in data){
                                item = data[i];
                                if(item.id){
                                    birthdate = ftcalendar.stringToDate(item.birthdate);
                                    birthMonth = birthdate.getMonth();
                                    birthDay = birthdate.getDate();
                                    bdYear = thisYear;
                                    if(birthMonth < thisMonth){
                                         bdYear ++;
                                    }
                                    bdDate =  new Date(bdYear, birthMonth, birthDay);
                                    age = ftcalendar.yearsBetween(birthdate,bdDate);
                                    daysUntil = ftcalendar.daysBetween(today,bdDate);
                                    if(daysUntil == 0){
                                        bdText = "Today";
                                    } else if (daysUntil == 1){
                                        bdText = "Tomorrow";
                                    } else {
                                        bdText = "On " + birthdate.format('m/d');
                                    }
                                    $report.append('<div class="ftd_report_item ftd_days_until_'+daysUntil+'"><span>'+item.full_name+'</span><span> turns '+age+' '+bdText+'</span></div>');
                                }
                            }
                        } else {
                            $report.append('<div class="ftd_report_item"><span>No upcomming birthdays</span></div>');
                        }
                    } else {
                        //console.log("Birthday Data has not changed");
                    }
                }
            }
        }
        if(options.clubCal){
            ajaxCommand.clubCal = true;
        }
        
        ftapi.get(ajaxCommand,{ccq:true});
        
    }
    
    var _visibleForeTeesAnnouncements = function($container, opt, opt2){
        
        var today = ftapi.getClubDate();
        
        var defaultOptions = {
            title:'Announcements',
            titleFontFrom:null, // jquery object where you would like to copy fonts from
            titleStyles:{},
            auto_load_id:0
        }

        var options = $.extend(true, {}, defaultOptions, opt, opt2), $caption = _getOrAdd('ftd_caption', $container, '<span><b>'+options.title + '</b></span>');
        
        var my_time = new Date().getTime();
        $container.data('ft_report_time',my_time);
        
        var ajaxCommand = {
            command:(options.show_active?'foreteesAnnouncementsByActiveDate':'foreteesAnnouncementsByMember'),
            success:function(data){
                if(my_time < $container.data('ft_report_time')){
                    // Another refresh happened for this report before I finished.  Abort!
                    //console.log("I've been outmoded!");
                } else {
                    var oldData = $container.data('ft_report_data');
                    $container.data('ft_report_data',data);
                    if(!ftapi.compareObjects(data,oldData)){
                        var $report = _getOrAdd('ftd_report_list', $container);
                        $report.empty();
                        var i, item, is_read, id, report_htm = '', highlight_active = options.highlight_active;
                        if(data.length){
                            for(i in data){
                                item = data[i];
                                id = item.id;
                                if(id){
                                    is_read = (highlight_active?!item.is_active:item.is_read);
                                    //report_htm += '<div data-ftitem="'+id+'" class="ftd_report_item ftd_clickable_item ftd_'+(is_read?'read':'unread')+'"><span>'+item.title+'</span><span class="ftd_list-small"></span></div>';
                                    report_htm += '<div data-ftitem="'+id+'" class="ftd_report_item ftd_clickable_item ftd_'+(is_read?'read':'unread')+'"><span>'+item.title+'</span><span class="ftd_date_item">'+ftcalendar.stringToDate(item.date_start).format('mm/dd/yyyy')+'</span></div>';
                                }
                            }
                        } else {
                            report_htm = '<div class="ftd_report_item"><span>No Announcements</span></div>';
                        }
                        $report.append(report_htm);
                        $report.find('.ftd_report_item').click(function(){
                            var $el = $(this), id = parseInt($el.attr('data-ftitem'),10), $body = $('body');

                            $body.foreTeesModal("pleaseWait");

                            var ajaxCommand = {
                                command:'foreteesAnnouncementByMember',
                                id:id,
                                success:function(announcement){
                                    $body.foreTeesModal('pleaseWait','close');
                                    if(!highlight_active && $el.hasClass('ftd_unread')){
                                        $el.addClass('ftd_read').removeClass('ftd_unread');
                                        var $unread = $report.find('.ftd_unread'), unread_count = $unread.length;
                                        if(!unread_count){
                                            $('#main .ft_announce_unread').remove();
                                        } else {
                                            $('#main .ft_announce_unread .ft_unread_count').text(unread_count);
                                        }
                                    }

                                    //var o = {
                                    //    $iframe:$()
                                    //};

                                    var resizeInterval = false;

                                    $body.foreTeesModal('alertNotice',{
                                        width:850,
                                        title:announcement.title,
                                        alertMode:false,
                                        message:'',
                                        init:function($modalObj){

                                            //var parentWindow = window;
                                            $('#ftAnnouncementFrame').remove();

                                            $('<iframe id="ftAnnouncementFrame" name="ftAnnouncementFrame" frameborder="0" src="../assets/static/empty.html"/>').load(function(){ // We need to use "empty.html" inorder to set the the referrer of the iframe, allowing vemo video to load
                                                var html = announcement.html, $iframeinner = $(this), $iframe_contents = $iframeinner.contents(), $iframe_head = $iframe_contents.find('head'), $iframe_body = $iframe_contents.find('body');
                                                if(ftIsIe8){
                                                    html = html
                                                    .replace(/<(header|footer|section|article|blockquote)([^>]+)class=["']([^"']*['"])/gi,function(a,a1,a2,a3){return '<div class="ie8'+a1+' '+a3+a2})
                                                    .replace(/<(header|footer|section|article|blockquote)/gi,function(a,a1){return '<div class="ie8'+a1+'"'})
                                                    .replace(/<\/(header|footer|section|article|blockquote)/gi,'</div');
                                                }
                                                $iframe_body.html(html);
                                                //var $head = $iframeinner.contents().find("head");
                                                $iframe_head.append('<title>'+announcement.title+'</title>');
                                                $iframe_head.append('<style> body { overflow:hidden; margin:0; padding:0;} '+announcement.css+' </style>');
                                                //setTimeout(function(){
                                                //    $(parentWindow).find('#ftAnnouncementFrame').height(900);
                                                //},1);
                                                
                                                
                                                
                                                
                                                var last_width = $iframe_body.prop('scrollWidth');
                                                //var last_height =  $iframe_contents.height();
                                                function __resizeIframe(){
                                                    //if(force_width){
                                                    //    $iframeinner.css('width','100%');
                                                    //}
                                                    var new_height = $iframe_contents.height();
                                                    if(new_height != $iframeinner.height()){
                                                        $iframeinner.height(new_height);
                                                    }
                                                    
                                                    //console.log("Set:"+$iframe_contents.height());
                                                    //$iframeinner.width( $iframe_body.prop('scrollWidth'));
                                                }
                                                
                                                
                                                
                                                setTimeout(function(){
                                                    __resizeIframe();
                                                    $modalObj.foreTeesModal("triggerResize");
                                                },1); // resize right away.
                                                if(!ftIsIe8){
                                                    // >= IE8
                                                    $iframe_body.resize(function(){
                                                        __resizeIframe();
                                                    });
                                                    resizeInterval = setInterval(function(){
                                                        __resizeIframe();
                                                    },100); // Check every 1/2 second
                                                    $(window).resize(function(){
                                                        var new_width = $iframe_body.prop('scrollWidth');
                                                        if(last_width != new_width){
                                                            last_width = new_width;
                                                            $iframeinner.height(200);
                                                            __resizeIframe();
                                                        }

                                                    })
                                                }
                                                
                                                
                                                //console.log("iframe body height:"+$iframe_body.height());
                                                //o.$iframe = $iframeinner;
                                                //$iframe.height($iframe_html.height());
                                                $modalObj.foreTeesModal("triggerResize");

                                            }).appendTo($modalObj);
                                        },
                                        allowContinue:true,
                                        continueButton:'Print',
                                        continueAction:function($modalObj){
                                            //$body.foreTeesModal('pleaseWait');
                                            if(document.all || window.PointerEvent){
                                                // IE needs to print this way
                                                var target = window.document.getElementById('ftAnnouncementFrame');
                                                target.focus();
                                                target.contentWindow.document.execCommand('print', false, null);
                                            } else {
                                                // Firefox needs to print this way.  Webkit should work as well
                                                window.frames['ftAnnouncementFrame'].focus();
                                                window.frames['ftAnnouncementFrame'].print();
                                            }
                                        },
                                        onClose:function($modalObj){
                                            $('#ftAnnouncementFrame').remove();
                                            clearInterval(resizeInterval);
                                        }
                                    });   
                                }
                            }

                            ftapi.get(ajaxCommand,{beforeError:function(){$body.foreTeesModal("pleaseWait","close")}});


                        });

                        $report.find('.ftd_report_item[data-ftitem='+options.auto_load_id+']').click();
                    } else {
                        //console.log("Announcement Data has not changed");
                    }
                }
            }
        }
        if(options.clubCal){
            ajaxCommand.clubCal = true;
        }
        
        ftapi.get(ajaxCommand,{ccq:true});
        
    }
    
    
    var _activityReservationsBarChart = function($container, opt, opt2){
        
        var today = new Date();
        
        var defaultOptions = {
            categories:['Total','Members','Guests'],
            date:today,
            activity_id:0,
            title:'Golf Reservations',
            titleFontFrom:null,
            titleStyles:{}
        }

        var options = $.extend(true, {}, defaultOptions, opt, opt2), tfs = _getTitleStyles(options);
        
        // Make sure it's a valid date object
        options.date = ftcalendar.stringToDate(options.date);
        
        var date = options.date, 
            $wrapper = _getOrAdd('ftd_3x2barGraph', $container), 
            $report = _getOrAdd('ftd_graph_container', $wrapper),
            oldOptions = $container.data('ft_report_options');
            
            
        var chart_structure = {
            chart: {
                type: 'bar',
                spacingTop:-13,
                //backgroundColor:'transparent',
                backgroundColor:'', // IE8 doesn't understand 'transparent''
                events: {
                    load: function(){
                        var chart = this;
                        chart.showLoading('...');
                        $report.data('ft_highchart',chart);

                        /*
                        chart.addSeries({
                            name: 'Tee Times',
                            data: [947, 156, 133]
                        });
                        chart.addSeries({
                            name: 'Events',
                            data: [635, 107, 31]
                        });
                        */

                    }
                }
            },
            title: {
                align: 'left',
                text: '<span class="ftd_report_title">'+options.title+'</span>',
                style: tfs,
                y:31,
                useHTML: true
            },
            subtitle: {
                align: 'right',
                verticalAlign: 'top',
                floating:true,
                text: '<span class="ftd_report_title ftd_report_subtitle">'+date.format('m/d/yyyy')+'</span>',
                style: tfs,
                y:26,
                useHTML: true
            },
            xAxis: {
                categories: options.categories
            },
            yAxis: {
                min: 0, //0
                title: {
                    enabled:false
                },
                labels: {
                    y:10,
                    overflow: 'justify'
                }
            },
            tooltip: {
                valueSuffix: ' reg.'
            },
            plotOptions: {
                bar: {
                    pointPadding: 0,
                    groupPadding: .05,
                    dataLabels: {
                        enabled: true,
                        inside:true,
                        //useHTML: true,
                        align: 'left',
                        color:'white',
                        formatter: function () {
                            return '<span style="color:white;text-shadow: 1px 1px 3px rgba(0, 0, 0, 1);">' + this.y + '</span>';
                        }
                    }
                }
            },
            legend: {
                align: 'center',
                verticalAlign: 'bottom',
                margin:0,
                y:6,
                padding:6
            },
            credits: {
                enabled: false
            },
            series: []
        };

        if(!ftapi.compareObjects(options,oldOptions)){
            // Options have changed since last update.  Clear report
            $container.data('ft_report_data',null);
            $report.highcharts(chart_structure);
            
        } else {
            //console.log(options.title + " options have not changed");
        }
        
        $container.data('ft_report_options',options);
        
        var highchart = $report.data('ft_highchart');
        var my_time = new Date().getTime();
        $container.data('ft_report_time',my_time);
        
        var ajaxCommand = {
            command:'clubActivitySignupCountsByDate',
            date_start:date.format('yyyy-mm-dd'),
            date_end:date.format('yyyy-mm-dd'),
            activity_id:options.activity_id,
            success:function(data){
                var oldData = $container.data('ft_report_data');
                $container.data('ft_report_data',data);
                if(my_time < $container.data('ft_report_time')){
                    // Another refresh happened for this report before I finished.  Abort!
                    //console.log("I've been outmoded!");
                } else {
                    if (!ftapi.compareObjects(data,oldData)){
                        if(data.length){
                            if(oldData !== null){
                                $report.highcharts(chart_structure);
                                highchart = $report.data('ft_highchart');
                            }
                            var result = data[0]; // First is the only that matters (and only that should exist)
                            var showEvent = true, showActivity = true;
                            if(!result.event_total) {
                                showEvent = false;
                            } else if(!result.activity_total && result.event_total) {
                                showActivity = false;
                            }
                            // CLear data
                            while(highchart.series.length > 0){
                                highchart.series[0].remove(true);
                            }
                            // Set data
                            highchart.addSeries({
                                name: result.activity_text,
                                data: [result.activity_total, result.activity_members, result.activity_guests],
                                visible: showActivity
                            });
                            highchart.addSeries({
                                name: result.event_text,
                                data: [result.event_total, result.event_members, result.event_guests],
                                visible: showEvent
                            });
                        } else {
                            // No data?
                        }
                    } else {
                        //console.log(options.title + " data has not changed");
                    }
                    highchart.hideLoading();
                }
            }
        }
        if(options.clubCal){
            ajaxCommand.clubCal = true;
        }

        ftapi.get(ajaxCommand,{ccq:true});
        
    }
    
    // Export public methods
    return {
        activityReservationsBarChart:_activityReservationsBarChart,
        upcommingBirthdays:_upcommingBirthdays,
        vipsWithReservations:_vipsWithReservations,
        visibleForeTeesAnnouncements:_visibleForeTeesAnnouncements
    };
    
})();

