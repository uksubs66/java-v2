/* 
 * Js used only in login
 */

// Fix viewport scale on android
var scale = 1 / window.devicePixelRatio;
var viewportTag = '';
//viewportTag += '<meta name="apple-mobile-web-app-capable" content="yes" />';
viewportTag += '<meta id="meta1" name="viewport" content="minimum-scale=1.0, width=device-width, maximum-scale=1.0, user-scalable=no"/>';
document.write(viewportTag);

$(document).ready(function() {
    var ios = navigator.userAgent.match(/(iPod|iPhone|iPad)/);
    $('.radioBarGroup label>input+span').on((ios?"touchstart":"mouseover"),function(e){
        var el = $(this);
        $('.ftHover').not(el).removeClass("ftHover");
        $(this)
        .addClass("ftHover").addClass("ftHoverActive")
        .one((ios?"touchend":"mouseout"),function(){$(this).removeClass("ftHoverActive");});
    }).on("mouseout",function(){$(this).removeClass("ftHover")});
    if(ios){
        $('body').on("touchstart",function(){
            $('.ftHover').not(".ftHoverActive").removeClass("ftHover");
        }); 
    }
    /*
    var scheme = window.location.protocol, host = window.location.hostname;
    if(scheme == "https:"){
        // Prepend "p." to hostname for proshop.
        $("body.LoginPrompt form#login input.login_button_lg").on("click", function(e){
            var username = $('input#user_name').val(), $form = $("form#login"), current_action = $form.attr("action");
            if(!current_action.match("^http")){
                if(username.match(/^proshop/i)){
                    // Proshop user
                    $form.attr("action", scheme+"//p."+host.replace(/^p\./i,"")+current_action);
                } else {
                    // Not proshop user
                    $form.attr("action", scheme+"//"+host.replace(/^p\./i,"")+current_action);
                }
            }
        })
    }
    */
});
