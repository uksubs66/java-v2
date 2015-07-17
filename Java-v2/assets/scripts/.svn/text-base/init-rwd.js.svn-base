/* 
 * Initialization for mobile / responsive mode
 */

/*************************************************************
 *
 * Initialize jQuery objects
 *  
 **************************************************************/
// jQuery start on DOM ready
/*
console.log(screen.width);
console.log($(window).width());
console.log(window.devicePixelRatio);
*/
$(document).ready(function() {
    
    adjustAnnounce();
    
    $('#rwdNav2 a[href="Dining_slot?action=new"]').click(function(){
        $('#rwdNav2>a#rwdNavButton').trigger('ft-close');
        $('body').foreTeesModal('pleaseWait','open', true);
    });
    
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
        
        $('#scrollUp').css("right", "-4px");
        $('#scrollUp').css("bottom", "35px");
        ftScrollActivationOffset = $(window).height() / 2;
    }

    if(ftScrollUtilize == true) {
        $(window).scroll(function() {
            if ($(this).scrollTop() > ftScrollActivationOffset) {
                if ($('#scrollUp').hasClass("scrollUp_show") == false) {
                    $('#scrollUp').css("opacity", "0.0");
                    $('#scrollUp').css("display", "block");
                    $('#scrollUp').fadeTo(ftScrollDuration, 0.4);
                    $('#scrollUp').addClass("scrollUp_show", 0);
                }
            } else {
                if ($('#scrollUp').hasClass("scrollUp_show") == true) {
                    $('#scrollUp').fadeTo(ftScrollDuration, 0.0);
                    $('#scrollUp').removeClass("scrollUp_show", 0);
                    $('#scrollUp').css("display", "none");
                }
            }
        });
        $('#scrollUp').mouseover(function() {
            $('#scrollUp').css("opacity", "0.8");
            //$('#scrollUp').fadeTo(100, 0.8);
        });
        $('#scrollUp').mouseout(function() {
            $('#scrollUp').css("opacity", "0.4");
            //$('#scrollUp').fadeTo(100, 0.4);
        });
    }
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
    
    // Stop navigation popup menus from affecting history
    $("#rwdNav li[aria-haspopup='true']>a[href='#']").click(function(e){
        e.preventDefault();
    });
    
    // follow_selected button
    $(".follow_selected").click(function(e){
       e.preventDefault();
       var el = $(this);
       var selector = el.attr("data-ftselector");
       var url;
       if(selector){
           var els = $(selector);
           url = els.filter(":checked,:selected").val();
       }
       if(url){
           window.location.href = url;
       }
       //console.log("GO:"+url);
    });
    
    // Tabs
    $(".rwdTabOrSelect a").click(function(e){
        var o = $(this);
        var c = o.closest('.rwdTabOrSelect');
        var p = o.parent();
        var sel = o.attr("href");
        var tab = $(sel);
        tab.siblings().removeClass("selected");
        tab.addClass("selected");
        p.siblings().removeClass("selected");
        p.addClass("selected");
        e.preventDefault();
        c.find('select').val(sel);
    });
    $(".rwdTabOrSelect select").change(function(e){
        var o = $(this);
        var sel = o.val();
        var c = o.closest('.rwdTabOrSelect');
        var a = c.find('a[href="'+sel+'"]');
        var p = a.parent();
        var tab = $(sel);  
        tab.siblings().removeClass("selected");
        tab.addClass("selected");
        p.siblings().removeClass("selected");
        p.addClass("selected");
        e.preventDefault();
    });
    
    // Navigation button
    $('#rwdNav2>a#rwdNavButton').unbind('click').on('click ft-close',function(e){
        var menu = $('#rwdNav2>ul');
        var button = $(this);
        if(menu.hasClass('active')){
            $('#rwdNavMask').removeClass('active');
            button.removeClass('active')
            .removeClass('ftB-36-Close')
            .addClass('ftB-36-Menu');
            menu.removeClass('active');
        } else if(e.type != "ft-close"){
            var mask = $('#rwdNavMask');
            if(!mask.length){
                mask = $('<div id="rwdNavMask"></div>');
                mask.click(function(e){
                    button.click();
                    e.preventDefault();
                });
                $('#pageHeader').before(mask);
            }
            mask.addClass('active');
            button.addClass('active')
            .removeClass('ftB-36-Menu')
            .addClass('ftB-36-Close');
            menu.addClass('active');
        }
        e.preventDefault();
    });
    
    // Activity select list
    $("#subNavSelect select").change(function(){
        window.location = $(this).find("option:selected").attr("data-fturl");
    });
    
    ftInitPageHelp($('body'));
    
    // Sort options
    $(".columnSort select[name=sortColumn]").change(function(){
        window.location.href = ftUpdateQueryString('sortBy', $(this).val(), window.location.href);
    });
    $(".columnSort select[name=sortOrder]").change(function(){
        if($(this).val() == "asc"){
            window.location.href = ftUpdateQueryString('desc', null, window.location.href);
        } else {
            window.location.href = ftUpdateQueryString('desc', '', window.location.href);
        }
    });
    
    // Search other member handicap deltas/reports
    $('.ftFindMemberHdcp').click(function(e){
        var obj = $(this);
        var inc20 = obj.parent().find('input[name=inc20]:checked');
        ftMemberSelectPopup(obj,{
            partnerSelect: false,
            memberSearch: true,
            requireGhin: true,
            onSelect:function(record, o){
                var form = {
                    todo:obj.attr('data-fthdcpdata'),
                    name:record.display,
                    back:'search'
                };
                if(inc20.length){
                    form.inc20 = 'yes';
                }
                $('body').foreTeesModal('pleaseWait',true);
                var formObj = $('<form action="Member_handicaps" method="post">' + ftObjectToForm(form) + "</form>");
                $("body").append(formObj);
                formObj.submit();
            }
        });
        e.preventDefault();
    });
    
    // Member reservation/tee time search tool
    $('.memberReservationSearch').foreTeesMemberSelect("init",{
        partnerSelect: true,
        memberSearch: true,
        onSelect:function(record, o){
            $('body').foreTeesModal('pleaseWait',true);
            var formObj = $('<form action="'+ftSetJsid("Member_searchmem")+'" method="post">' + ftObjectToForm({
                source:'main',
                search_name:record.display,
                search_data:''
            }) + "</form>");
            $("body").append(formObj);
            formObj.submit();
        }
    });
    
    // Member dining reservation search tool
    $('.memberDiningReservationSearch').foreTeesMemberSelect("init",{
        partnerSelect: true,
        memberSearch: true,
        onSelect:function(record, o){
            $('body').foreTeesModal('pleaseWait',true);
            var formObj = $('<form action="'+ftSetJsid("Dining_home")+'" method="get">' + ftObjectToForm({
                search:'',
                search_name:record.display,
                search_data:record.dining_id,
                commit_sub:'Search',
                bud:record.display+':'+record.dining_id
            }) + "</form>");
            $("body").append(formObj);
            formObj.submit();
        }
    });
    
    // Member select date picker
    //ftActivateElements($(".rwdDateSelect"));
    
    // Partner list management
    var ftPlList = [];
    $('.ftPlManage').each(function(){
        var obj = $(this);
        var activityId = obj.attr('data-ftPlActivityId');
        var activityName = obj.attr('data-ftPlActivityName');
        if(activityId.length){
            ftPlList.push({id:activityId,name:activityName});
        }
        ftPlManage(obj);
    });
    if(ftPlList.length){
        var plMsTitle = $('<div class="ftPlMsTitle"></div>');
        var plCloseButton = $('<a href="#" class="standard_button">Close</a>');
        var plOverlay = $('<div class="ftPlOverlay"></div>');
        var plAddPartner = $('<div class="ftPlAddPartner"></div>');
        var plMemberSelect = $('<div class="ftPlMemberSelect"></div>');
        var pListSelect = $('<div class="ftPlSelect"></div>');
        var activityId = $.fn.foreTeesSession('get','activity_id');
        for(var i=0; i < ftPlList.length; i++){
            pListSelect.append('<label onclick="" class="standard_button"><input type="checkbox" class="ftPlId_'+ftPlList[i].id+'" value="'+ftPlList[i].id+'"/>'+ftPlList[i].name+'</label>');
        }
        if(ftPlList.length < 2) {
            pListSelect.css('display','none'); // Dont show list if only 1
        }
        pListSelect.find('.ftPlId_'+activityId).prop('checked', true); // Select active activity by default
        plAddPartner.append(plMsTitle);
        plMsTitle.append(plCloseButton);
        plMsTitle.append('<span>Select <span></span> Partner</span>');
        plAddPartner.append(pListSelect);
        plAddPartner.append(plMemberSelect);
        var plContainer = $('.ftPlContainer');
        plContainer.prepend(plOverlay);
        plContainer.prepend(plAddPartner);
        plContainer.click(function(e){
            //e.preventDefault(); // Fix IE mobile event bubble
        });
        plCloseButton.add(plOverlay).click(function(e){
            plContainer.removeClass('ftPlSelectOpen');
            e.preventDefault();
        });
        plAddPartner.foreTeesModal("resize",{
            box:plAddPartner,
            maxWidth:290,
            type:"ftPlAddPartner",
            minHeight:250,
            maxHeight:400,
            scaleHeight:true
        });
        plAddPartner.foreTeesModal("bindResize");
        plMemberSelect.foreTeesMemberSelect("init",{
            memberSearch: true,
            onSelect:function(record, o){
                var url = 'Member_partner?list&addPartner&json=true&partner_id='+record.username
                var parms = '';
                var plActivityList = [];
                pListSelect.find('input:checked').each(function(){
                    parms += '&activity_'+$(this).val();
                    plActivityList.push($(this).val());
                });
                if(plActivityList.length){
                    // Add member to list
                    $.ajax({
                        url: url+parms,
                        dataType: 'json',
                        success: function(data){
                            // Re-init lists for activities
                            plContainer.removeClass('ftPlSelectOpen');
                            setTimeout(function(){
                                for(var i=0; i < plActivityList.length; i++){
                                    ftPlManage($('.ftPlActivityId_'+plActivityList[i]));
                                    // we could add code here to scroll to the newly added partner
                                }
                            },1);
                        },
                        error: function(xhr){
                            ft_logError("Error adding member to partner list", xhr);
                            // console.log("error recv. partner list");
                            obj.foreTeesModal("ajaxError", {
                                context:this,
                                errorThrown:"Unable to add member to partner list",
                                tryAgain:function(){
                                    ftPlManage(obj);
                                },
                                allowRefresh: true
                            });  
                        }
                    });
                }
            }
        });
    }
    
    
});

