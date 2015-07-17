/* 
 * 
 * Desktop js
 * 
 */

// Page help
function ftInitPageHelp(o){
    // Do nothing -- here for compatibility with RWD
}

// If a mobile browser, fix any select tags with a size attribute to get around bug in webkit mobile
jQuery.fn.foreTeesFixMobileSelect = function() {
    if(ftIsMobile()){
        $(this).each(function(){
            $(this).find("select[size]").removeAttr("size");
            $(this).height("auto");
        });
    }
}

// If a mobile browser, fix any select tags with a size attribute to get around bug in webkit mobile
jQuery.fn.foreTeesTableStyle = function(options) {
    var options = {
        columns: {
            "/^Time$/i":{
                th:"text-align: center;",
                td:"text-align: center;"
            },
            "/^\\#$/i":{
                th:"text-align: center;",
                td:"text-align: center;"
            },
            "/^F\\/B$/i":{
                th:"text-align: center;",
                td:"text-align: center;"
            },
            "/^C\\/W$/i":{
                th:"text-align: center;",
                td:"text-align: center;"
            }
        },
        selectors: [""]
    }
    $(this).each(function(){
        
        });
}