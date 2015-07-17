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
    
    // Set-up dining and lesson slot page completetion redirects.
    if(
        window.location.pathname.match(/.*\/Member_lesson/)
        ||
        window.location.pathname.match(/.*\/Dining_slot/)
        ||
        (window.location.pathname.match(/.*\/Dining_home/) && ftGetUriParam('event_popup') !== null)
        ){
       //console.log("setting up return path");
        // We're on a Dining slot page
        var dflt = ftChangeFile( "Member_announce" ); //  Where we'll go if we can't figure it out
        var rtUrl = ftGetUriParam('rtUrl');
        if(rtUrl == null){
            //console.log('user referrer');
            // First hit to the slot page.  Set return url the same as the referrer.
            rtUrl = document.referrer;
        }
        //console.log('found:'+rtUrl);
        
        if(!(
            // These are servlets that are O.K. to return to.
            rtUrl.match(/.*\/Member_announce/)
            ||
            rtUrl.match(/.*\/Member_teelist/)
            ||
            //rtUrl.match(/.*\/Member_sheet/)
            //||
            //rtUrl.match(/.*\/Member_select/)
            //||
            rtUrl.match(/.*\/Member_teelist_list/)
            ||
            (!window.location.pathname.match(/.*\/Member_lesson/)
            &&
            (
                rtUrl.match(/.*\/Member_events/)
                ||
                rtUrl.match(/.*\/Dining_home\?view_events/)
            )
            )
        )){
        //if(rtUrl.match(/.*\/servlet\/Login/) !== null || rtUrl.match(/.*\/Dining_slot/) !== null || rtUrl == null || !rtUrl.length){
            // Got a problem.  referrer is not on whitelist
            //console.log('Unusable rtUrl passed');
            rtUrl = dflt;
        }
        rtUrl = ftSetJsid(rtUrl);
        //console.log('using:'+rtUrl);
        //console.log('return url='+rtUrl);
        //console.log('Overriding ftReturnToCallerPage');
        //console.log(ftReturnToCallerPage);
        ftReturnToCallerPage = function(){
            //console.log('returning to:'+rtUrl);
            //window.location = rtUrl;
            ft_historyBack(rtUrl);
        }
        
        var free_lesson = $('form[action^="Member_lesson"] input[name="cancel"]');
        if(free_lesson.length){
            ftFreeLessonOnUnload = true;
            //console.log("binding unload");
            ftFreeLessonOnUnloadformValues = free_lesson.first().closest("form").serialize() + "&cancel";
            //console.log(ftFreeLessonOnUnloadformValues);
            $(window).unload(function(){
                // Free lesson on exit
                //console.log("starting unload");
                //console.log(ftFreeLessonOnUnloadformValues);
                if(ftFreeLessonOnUnload){

                    if(free_lesson.length){
                        var formValues = free_lesson.first().serialize();
                        $.ajax({
                            type: 'POST',
                            url: "Member_lesson",
                            data: ftFreeLessonOnUnloadformValues,
                            async: false,
                            dataType: 'text',
                            success: function(data){
                                //console.log("did unload");
                                //console.log(data);
                            },
                            error: function(xhr){
                            }
                        });
                    }
                }
            });
        }
        
        $('form[action^="Member_lesson"]').submit(function(){
            // dont automatically free lesson if a form is submitted
            ftFreeLessonOnUnload = false;
        });
        
        //Redirect completions for Member lesson
        $('.follow_return_path').click(function(){
            ft_historyBack(rtUrl);
            return false;
        });
        $('.auto_click_in_5').first().each(function(){
            setTimeout(ft_historyBack(rtUrl),5000);
        });
        $('form[action^="Member_lesson"],form[action="Dining_slot"]').each(function(){
            $(this).attr('action', ftUpdateQueryString("rtUrl", rtUrl, $(this).attr('action'), true));
        });
        $('a[href^="Member_lesson"],a[href^="Dining_slot"]').each(function(){
            $(this).attr('href', ftUpdateQueryString("rtUrl", rtUrl, $(this).attr('href'), true));
            if(ftGetUriParam('event_popup') != null && document.referrer.match(/^http[s]*:\/\/[^\.]*\.*foretees.com\//i)){
                $(this).attr('target','_parent');
                //console.log("dr:"+document.referrer);
            }
        });

    }
    
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

    // Dining event modals
    $("a.dining_event_modal").click(function(event){
        var o = $(this);
        var link = o.attr("data-ftlink");
        var title =  o.text();
        o.foreTeesModal("linkModal",{
            link:link,
            title:title
        });
        return false;
    });

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
    
});