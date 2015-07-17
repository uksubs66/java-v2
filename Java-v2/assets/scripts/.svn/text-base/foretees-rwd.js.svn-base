/* 
 * 
 * Responsive/Mobile js
 * 
 */

// Fix viewport scale on android
var scale = 1 / window.devicePixelRatio;
var viewportTag = '';
viewportTag += '<meta name="apple-mobile-web-app-capable" content="yes" />';
viewportTag += '<meta id="meta1" name="viewport" content="minimum-scale=1.0, width=device-width, maximum-scale=1.0, user-scalable=no"/>';
document.write(viewportTag);



ftOrientationWidth = $(window).width();
/*
$(window).on('orentationchange',function(e){
    switch (e.orientation) {
        case 'portrait':
            break;
        case 'landscape':
            break;
    }
});
*/
$(window).on('ft-resize',function(){
    
    if(window.matchMedia){
        // IE8
        var width2 = $(document).width();
        var width = $(window).innerWidth();
        width = width - (width2-width);
    } else {
        var width = $(window).innerWidth();
    }
    //console.log('width:'+width);
    //console.log('width2:'+width2);
    if(ftVar.lww != width){
        ftVar.lww = width;
        
        if(ftVar.resizeTimer){
            clearTimeout(ftVar.resizeTimer);
        }
        $(window).trigger('ft_widthchange');
        ftVar.resizeTimer = setTimeout(function(){
            // Stuff we'll do on every resize, but want to delay
            adjustAnnounce();
            //ftFixCalendarCells();
        },200);
        
        //ftFixCalendarCells();
        
        var screenMode = $('#rwdScreenMode').css("max-width");
        $('#rwdNav2>a#rwdNavButton').trigger('ft-close');
        
        switch (screenMode) {
            case "1000px":
                break;
        }
        
        // Merge/unmerge member select calendars if needed
        var dpc2 = $('.calendar.secondary');
        var mw = 580;
        var cCon = $('.Member_select #member_select_calendars');
        if(!cCon.length){
            cCon = $('.Member_gensheets div.member_sheet.tabular_container');
            mw = 890;
        }
        if(cCon.innerWidth() <= mw){
            //console.log('<974:'+width);
            if(dpc2.length && !dpc2.hasClass('rwdHide')){
                //console.log('found');
                var dp1 = $('.calendar.primary .hasDatepicker');
                dpc2.addClass('rwdHide');
                $('#main').addClass('singleCalendar');
                if(dp1.length){
                    var op = dp1.data('ftCalData');
                    if(op.lastHidePrevNext){
                        dp1.datepicker('option',{
                            maxDate:op.endDateSingle,
                            hideIfNoPrevNext:false
                        }).datepicker('refresh');
                        op.lastEndDate = op.endDateSingle;
                        op.lastHidePrevNext = false;
                    }
                    if(!dp1.data('ftDidScale')){
                        dp1.data('ftDidScale',true);
                        if(dp1.parent().attr('data-ftdefaultdate')){
                            //console.log(dp1.parent().attr('data-ftdefaultdate'));
                            dp1.parent().addClass('showDefaultDate');
                            dp1.datepicker('setDate',ftStringDateToDate(dp1.parent().attr('data-ftdefaultdate'))).datepicker('refresh');
                        }
                    }
                     
                }
            }
        } else {
            if(dpc2.length && dpc2.hasClass('rwdHide')){
                //var dp1 = $('#member_select_calendar1 .hasDatepicker');
                dpc2.removeClass('rwdHide');
                $('#main').removeClass('singleCalendar');
            //if(dp1.length){
            //    var op = dp1.data('ftCalData');
            //dp1.datepicker('option','maxDate',op.endDate).datepicker('refresh');
                     
            //}
            }
        }
    }
});

function ftMemberSelectPopup(obj,opt){
    var obn = 'ftMemberSelectPopup';
    var popUp = obj.data(obn);
    var overlay = obj.data(obn+'Ol');
    if(!opt.popupTitle){
        opt.popupTitle = 'Find Member';
    }
    if(!popUp){
        overlay = $('<div class="ftMsPopupOverlay"></div>');
        popUp = $('<div class="ftMsPopup"><div class="ftPlMsTitle"><a href="#" class="standard_button">Close</a><span>'+opt.popupTitle+'<span></div><div class="ftMemberPopupSelect"></div></div>');
        $('body')
        .append(overlay)
        .append(popUp);
        var selObj = popUp.find('.ftMemberPopupSelect');
        overlay.click(function(e){
            $(window).trigger('ft-resize');
            e.preventDefault();
        });
        popUp.find('.standard_button').click(function(e){
            popUp.addClass('hide');
            overlay.addClass('hide');
            $(window).trigger('ft-resize');
            e.preventDefault();
        });
        selObj.foreTeesMemberSelect("init",opt);
        selObj.foreTeesModal("resize",{
            box:popUp,
            maxWidth:290,
            minHeight:250,
            maxHeight:400,
            scaleHeight:true,
            type:"ftMsPopup"
        });
        selObj.foreTeesModal("bindResize");
        obj.data(obn, popUp);
        obj.data(obn+'Ol', overlay);
    } else {
        popUp.removeClass('hide');
        overlay.removeClass('hide');
        $(window).trigger('ft-resize');
    }
    ftSizeCheck();
}

// Partner list management
function ftPlManage(obj) {
    var activityId = obj.attr('data-ftPlActivityId');
    var activityName = obj.attr('data-ftPlActivityName');
    var hdcpTrack = obj.attr('data-ftPlTrackHdcp');
    var displayHdcp = obj.attr('data-ftPlDisplayHndcp');
    var userId = $.fn.foreTeesSession('get','user');
    if(obj.length && activityId.length){
        var url = "data_loader?json_mode=true&name_list=true&letter=partners&activityId="+activityId;
        var pList = $('<ul class="partnerList" data-ftActivityId="' + activityId + '" data-ftUserId="' + userId + '"></ul>');
        var sortButton = $('<a href="#" class="standard_button alphasort_icon">Alphabetize</a>');
        var addButton = $('<a href="#" class="standard_button add_icon">Add Partner</a>');
        var plContainer = obj.closest('.ftPlContainer');
        addButton.click(function(e){
            var pListSelect = plContainer.find('.ftPlSelect');
            var memberSelectObj = plContainer.find('.ftPlMemberSelect');
            pListSelect.find('input').prop('checked', false);
            pListSelect.find('.ftPlId_'+activityId).prop('checked', true);
            plContainer.addClass('ftPlSelectOpen');
            plContainer.find('.ftPlMsTitle > span > span').html(activityName);
            $(window).trigger('ft-resize');
            memberSelectObj.foreTeesMemberSelect("focus");
            e.preventDefault();
        });
        var plHdcpTrack = $('');
        if(hdcpTrack == 'yes'){
            plHdcpTrack = $('<label onclick="" class="ftPlHdcpTrack standard_button"><span>Show Partner\'s Handicap Index</span></label>');
            var plHdcpTrackInput = $('<input type="checkbox" value="1"/>');
            plHdcpTrack.prepend(plHdcpTrackInput);
            plHdcpTrackInput.prop('checked',displayHdcp == '1');
            plHdcpTrackInput.change(function(e){
                // Change hdcp status
                $.ajax({
                    url: 'Member_partner?json=true&updatehndcp&handicap&activity_id=' + activityId,
                    dataType: 'json',
                    success: function(data){
                        // Re-init re-ordered list out of context
                        obj.attr('data-ftPlDisplayHndcp',(data.result?'1':'0'));
                        setTimeout(function(){
                            ftPlManage(obj);
                        },1);
                    },
                    error: function(xhr){
                        ft_logError("Error setting display hdcp", xhr);
                        // console.log("error recv. partner list");
                        obj.foreTeesModal("ajaxError", {
                            context:this,
                            errorThrown:"Unable to set display hdcp",
                            tryAgain:function(){
                                ftPlManage(obj);
                            },
                            allowRefresh: true
                        });  
                    }
                });
            });
        }
        sortButton.click(function(e){
            if (confirm('This will reset this partner list to alphabetical order.  Are you sure you want to do this?')) {
                // Sort list of partners
                $.ajax({
                    url: 'Member_partner?json=true&list&resetOrder&user_id=' + userId + '&activity_id=' + activityId,
                    dataType: 'json',
                    success: function(data){
                        // Re-init re-ordered list out of context
                        setTimeout(function(){
                            ftPlManage(obj);
                        },1);
                    },
                    error: function(xhr){
                        ft_logError("Error sorting partner list", xhr);
                        // console.log("error recv. partner list");
                        obj.foreTeesModal("ajaxError", {
                            context:this,
                            errorThrown:"Unable to sort partner list",
                            tryAgain:function(){
                                ftPlManage(obj);
                            },
                            allowRefresh: true
                        });  
                    }
                });
            };
            e.preventDefault();
        });
        
        // Update list of partners
        $.ajax({
            url: url,
            dataType: 'json',
            success: function(data){
                for(var key in data){ // Build the list
                    var record = data[key]
                    var pLi = $("<li class=\"ui-state-default\" data-ftId=\""+record.id+"\" data-ftPartnerMtype=\"" + record.m_type + "\" data-ftPartnerName=\"" + record.name_only + "\" data-ftPartnerId=\"" + record.partner_id + "\">");
                    pLi.append("<div class=\"sortItem\"><a class=\"deleteButtonSmall\" href=\"#\" alt=\"Remove partner\" title=\"Remove partner\"><b>Remove</b></a><span style=\"" + record.style + "\">" + record.name + "</span><div class=\"sortHandle\"><div class=\"ui-icon ui-icon-arrowthick-2-n-s\"></div></div></div>");
                    pList.append(pLi);
                }
                obj
                .empty()
                .append(sortButton)
                .append(addButton)
                .append(plHdcpTrack)
                .append(pList);
                ftPartnerSort(pList);
            },
            error: function(xhr){
                ft_logError("Error loading manage partner list", xhr);
                // console.log("error recv. partner list");
                obj.foreTeesModal("ajaxError", {
                    context:this,
                    errorThrown:"Unable to load partner data",
                    tryAgain:function(){
                        ftPlManage(obj);
                    },
                    allowRefresh: true
                });  
            }
        });
    }
}

// Page help
function ftInitPageHelp(obj){
    obj.find(".pageHelp").each(function(){
        var phc = $(this);
        if(!phc.data("ftPh-init")){
            phc.data("ftPh-init",true);
            var button = $('<a class="pageHelpHeader inactive" href="#"><span>'+phc.attr("data-ftHelpTitle")+'</span></a>');
            button.click(function(e){
                var o = $(this);
                var wwStart = $(window).innerWidth();
                o.toggleClass('active');
                if(o.hasClass('active')){
                    o.removeClass('inactive');
                    phc.addClass('inactive');
                } else {
                    o.addClass('inactive');
                    phc.removeClass('active');
                }
                if(wwStart != $(window).innerWidth()){
                    // Scroll bar appeared or disappeared
                    // tell any modals to resize/position
                    $(window).trigger('ft-resize');
                }
                ftSizeCheck();
                e.preventDefault();
            });
            phc.before(button);
            if(phc.hasClass("activeOnLoad")){
                button.click();
            }
        }
    });
}
function adjustAnnounce(){
    // Modify content, if hasn't been done already
    $('#rwd_wrapper .announcement_container:not(.rwdAdjusted)').each(function(){
        var el = $(this);
        el.addClass('rwdAdjusted');
        var secondary = el.find('.widget_list_left .block_container:not(:empty)').slice(2); // Everything after second left block
        secondary.addClass('rwdSecondary');
        var secCopy = secondary.clone();
        el.find('.widget_list_primary .block_container:last-child').after(secCopy);
    });
    // Check if any images need margins adjusted
    $('#rwd_wrapper .announcement_container img[style*=margin]').each(function(){
        var el = $(this);
        var parentWidth = Math.ceil(el.parent().width());
        var elWidth = Math.floor(el.outerWidth(true));
        if( elWidth > parentWidth || (elWidth == parentWidth && el.hasClass('ft-hzCollapse'))){
            el.addClass('ft-hzCollapse');
        } else {
            el.removeClass('ft-hzCollapse');
        }
    });
}
/*
 * TotalStorage
 *
 * Copyright (c) 2012 Jared Novack & Upstatement (upstatement.com)
 * Dual licensed under the MIT and GPL licenses:
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl.html
 *
 * Total Storage is the conceptual the love child of jStorage by Andris Reinman,
 * and Cookie by Klaus Hartl -- though this is not connected to either project.
 *
 * @name $.totalStorage
 * @cat Plugins/Cookie
 * @author Jared Novack/jared@upstatement.com
 * @version 1.1.2
 * @url http://upstatement.com/blog/2012/01/jquery-local-storage-done-right-and-easy/
 */
/* 
(function(c,h){var e,d;if("localStorage"in window)try{d="undefined"===typeof window.localStorage?h:window.localStorage,e="undefined"==typeof d||"undefined"==typeof window.JSON?!1:!0}catch(j){e=!1}c.totalStorage=function(b,a){return c.totalStorage.impl.init(b,a)};c.totalStorage.setItem=function(b,a){return c.totalStorage.impl.setItem(b,a)};c.totalStorage.getItem=function(b){return c.totalStorage.impl.getItem(b)};c.totalStorage.getAll=function(){return c.totalStorage.impl.getAll()};c.totalStorage.deleteItem=
function(b){return c.totalStorage.impl.deleteItem(b)};c.totalStorage.impl={init:function(b,a){return"undefined"!=typeof a?this.setItem(b,a):this.getItem(b)},setItem:function(b,a){if(!e)try{return c.cookie(b,a),a}catch(g){console.log("Local Storage not supported by this browser. Install the cookie plugin on your site to take advantage of the same functionality. You can get it at https://github.com/carhartl/jquery-cookie")}var f=JSON.stringify(a);d.setItem(b,f);return this.parseResult(f)},getItem:function(b){if(!e)try{return this.parseResult(c.cookie(b))}catch(a){return null}b=
d.getItem(b);return this.parseResult(b)},deleteItem:function(b){if(!e)try{return c.cookie(b,null),!0}catch(a){return!1}d.removeItem(b);return!0},getAll:function(){var b=[];if(e)for(var a in d)a.length&&b.push({key:a,value:this.parseResult(d.getItem(a))});else try{var g=document.cookie.split(";");for(a=0;a<g.length;a++){var f=g[a].split("=")[0];b.push({key:f,value:this.parseResult(c.cookie(f))})}}catch(h){return null}return b},parseResult:function(b){var a;try{a=JSON.parse(b),"undefined"==typeof a&&
(a=b),"true"==a&&(a=!0),"false"==a&&(a=!1),parseFloat(a)==a&&"object"!=typeof a&&(a=parseFloat(a))}catch(c){a=b}return a}}})(jQuery);
*/