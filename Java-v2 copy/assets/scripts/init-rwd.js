/* 
 * Initialization for mobile / responsive mode
 */

/*************************************************************
 *
 * Initialize jQuery objects
 *  
 **************************************************************/
// jQuery start on DOM ready

$(document).ready(function() {

    var nav2 = $('#rwdNav2>ul');
    nav2.accordion({
        heightStyle: "content",
        active: false,
        collapsible: true
    });
    nav2.find('li>a.ui-accordion-header:last-child').addClass('noChildren').click(function(e){
        if(!e.isPropagationStopped()){
            window.location.href = $(this).attr('href');
        }
    });
    
    // Navigation button
    $('#rwdNav2>a#rwdNavButton').unbind('click').on('click',function(e){
        var menu = $('#rwdNav2>ul');
        if(menu.hasClass('active')){
            $(this).removeClass('active')
            .removeClass('ftB-36-Close')
            .addClass('ftB-36-Menu');
            menu.removeClass('active');
        } else {
            $(this).addClass('active')
            .removeClass('ftB-36-Menu')
            .addClass('ftB-36-Close');
            menu.addClass('active');
        }
        return false;
    });
    
    // Activity select list
    $("#subNavSelect select").change(function(){
        window.location = $(this).find("option:selected").attr("data-fturl");
    });
    
    // Page help
    $(".pageHelp").each(function(){
        var phc = $(this);
        phc.before($('<a class="pageHelpHeader"><span>'+phc.attr("data-ftHelpTitle")+'</span></a>'));
    });
});

