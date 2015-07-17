/* 
 * Initialization for desktop / static mode
 */


/*************************************************************
 *
 * Initialize jQuery objects
 *  
 **************************************************************/
// jQuery start on DOM ready
$(document).ready(function() {
    
    /*
    // If we're in an iframe, we need to append the jsessionid to everything
    $('a').each(function(){
        $(this).attr('href', ftSetJsid($(this).attr('href')));
    });
    $('form').each(function(){
        $(this).attr('action', ftSetJsid($(this).attr('action')));
    });
    */
   
   //console.log("desktop init");
    
    // On mobile platforms, force the width of the body to the size of the window, if the window is larger than the body
    if(ftIsMobile()){
        var windowWidth = window.innerWidth;
        //console.log("ww:"+windowWidth+":dw:"+$("body").width());
        if($("body").width() < windowWidth){
            $("body").width(windowWidth);
        }
    }

    // Fix select elements with size > 1 on mobile browsers
    $("body").foreTeesFixMobileSelect();

    // Simulate TopUp's modal behavior
    $("[class^=tu_iframe_]").click(function(event){

        var tu_obj = $(this);
        var tu_class_arr = tu_obj.attr("class").split(" ");
        var tu_height = 400;
        var tu_width = 620;

        for(var i=0; i<tu_class_arr.length; i++) {
            if(tu_class_arr[i].substr(0,10)=="tu_iframe_"){
                var tu_arr = tu_class_arr[i].split("_");
                if(tu_arr.length == 3){
                    tu_arr = tu_arr[2].split("x");
                    if(tu_arr.length == 2){
                        tu_width = parseInt(tu_arr[0], 10);
                        tu_height = parseInt(tu_arr[1], 10);
                    }
                }
            }
        }
        tu_obj.fancybox({
            'transitionIn':'elastic',
            'transitionOut':'elastic',
            'height':tu_height,
            'width':tu_width,
            'type':'iframe',
            'centerOnScroll':true,
            'changeSpeed':50,
            'margin':13,
            'padding':0
        });

        event.preventDefault();
        // For some reason, the first click fails. If this is the first try, send another click. 
        if(tu_obj.data('tu_click_track')!=true){
            tu_obj.data('tu_click_track',true)
            tu_obj.click();
        }

    });
    
    // Weather widget
    $('div.announcement_container div.weather_widget_accuweather_wide').first().each(function(){
        setTimeout(
        function(){
            $('div.announcement_container div.weather_widget_accuweather_wide').first().each(
            function(){
                var obj = $(this).find("#NetweatherContainer");
                var zipcode = $.fn.foreTeesSession("get","zipcode");
                var weatherObj = $('<script src="http://netweather.accuweather.com/adcbin/netweather_v2/netweatherV2ex.asp?partner=netweather&tStyle=whteYell&logo=1&zipcode='+zipcode+'&lang=eng&size=11&theme=blue&metric=0&target=_blank"></script>');
                obj.append(weatherObj);
        })}, 250);
    });
    
    //console.log("before sort");
    
    ftPartnerSort($( "ul.partnerList" ));
    
    // Stop navigation popup menus from affecting history
    $("#topnav li[aria-haspopup='true']>a[href='#']").click(function(e){
        e.preventDefault();
    });
   
    
});