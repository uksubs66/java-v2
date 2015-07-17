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

// Global vars
var ftVar = {}; 

$(window).resize(function(){
    
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
        var screenMode = $('#rwdScreenMode').css("max-width");
        $('#rwdNav2>ul').removeClass('active');
        $('#rwdNav2>a#rwdNavButton').removeClass('active')
        .removeClass('ftB-36-Close')
        .addClass('ftB-36-Menu');
        
        switch (screenMode) {
            case "1000px":
                break;
        }
        
        // Merge/unmerge member select calendars if needed
        var dpc2 = $('#member_select_calendar2');
        if(width <= 900){
            //console.log('<974:'+width);
            if(dpc2.length && !dpc2.hasClass('rwdHide')){
                //console.log('found');
                 var dp1 = $('#member_select_calendar1 .hasDatepicker');
                 dpc2.addClass('rwdHide');
                 $('#main').addClass('singleCalendar');
                 if(dp1.length){
                     var op = dp1.data('ftCalData');
                     if(op.lastHidePrevNext){
                         dp1.datepicker('option',{maxDate:op.endDateSingle,hideIfNoPrevNext:false}).datepicker('refresh');
                         op.lastEndDate = op.endDateSingle;
                         op.lastHidePrevNext = false;
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
 
(function(c,h){var e,d;if("localStorage"in window)try{d="undefined"===typeof window.localStorage?h:window.localStorage,e="undefined"==typeof d||"undefined"==typeof window.JSON?!1:!0}catch(j){e=!1}c.totalStorage=function(b,a){return c.totalStorage.impl.init(b,a)};c.totalStorage.setItem=function(b,a){return c.totalStorage.impl.setItem(b,a)};c.totalStorage.getItem=function(b){return c.totalStorage.impl.getItem(b)};c.totalStorage.getAll=function(){return c.totalStorage.impl.getAll()};c.totalStorage.deleteItem=
function(b){return c.totalStorage.impl.deleteItem(b)};c.totalStorage.impl={init:function(b,a){return"undefined"!=typeof a?this.setItem(b,a):this.getItem(b)},setItem:function(b,a){if(!e)try{return c.cookie(b,a),a}catch(g){console.log("Local Storage not supported by this browser. Install the cookie plugin on your site to take advantage of the same functionality. You can get it at https://github.com/carhartl/jquery-cookie")}var f=JSON.stringify(a);d.setItem(b,f);return this.parseResult(f)},getItem:function(b){if(!e)try{return this.parseResult(c.cookie(b))}catch(a){return null}b=
d.getItem(b);return this.parseResult(b)},deleteItem:function(b){if(!e)try{return c.cookie(b,null),!0}catch(a){return!1}d.removeItem(b);return!0},getAll:function(){var b=[];if(e)for(var a in d)a.length&&b.push({key:a,value:this.parseResult(d.getItem(a))});else try{var g=document.cookie.split(";");for(a=0;a<g.length;a++){var f=g[a].split("=")[0];b.push({key:f,value:this.parseResult(c.cookie(f))})}}catch(h){return null}return b},parseResult:function(b){var a;try{a=JSON.parse(b),"undefined"==typeof a&&
(a=b),"true"==a&&(a=!0),"false"==a&&(a=!1),parseFloat(a)==a&&"object"!=typeof a&&(a=parseFloat(a))}catch(c){a=b}return a}}})(jQuery);