/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

// jQuery start on DOM ready
var ft_IE6selectFixStack = [];
$(document).ready(function() {

    // Fix IE6/7 navigation issues
    // IE6/7 will not properly set children's width to 100% if parent's width is auto.
    $("#topnav li").mouseover(function(){
        if(typeof($(this).data("ft-topnavIeHack")) == "undefined" || $(this).data("ft-topnavIeHack") == false){
            $(this).data("ft-topnavIeHack", true);
            setTimeout($.proxy(function(){
                var obj = $(this);
                $(this).children("ul").each(function(){
                    if($(this).is(":visible")){
                        var maxwidth = 0;
                        $(this).children("li").each(function(){
                            if($(this).outerWidth(true) > maxwidth){
                                maxwidth = $(this).outerWidth();
                            }
                        });
                        $(this).width(maxwidth);
                    }else{
                        obj.data("ft-topnavIeHack", false);
                    }

                });
            },$(this)),1);
        }
    });
    
    // Set z-index on mouseover
    $("#topnav li").mouseover(function(){
        var tnc = $(this).closest("#topnav_container");
        var zi = $(this).find("ul").first().css("z-index");
        tnc.css("z-index",zi);
        $(this).css("z-index",zi);
        //console.log("setz:"+$(this).find("ul").first().css("z-index"));
    });
    $("#topnav li").mouseout(function(){
       var tnc = $(this).closest("#topnav_container");
       tnc.css("z-index",0);
       $(this).css("z-index",0);
        //console.log("unsetz:"+0);
    });

    // IE6/7 does not support display-table, etc.
    $(".tabular_cell").wrap("<td />");
    $(".tabular_row").wrap("<tr />");
    $(".tabular_container").each(function(){
        var tclass = $(this).attr("class")
        $(this).wrapInner('<table class="'+tclass+'" />');
    });
    

// End of jQuery on-load config
});
