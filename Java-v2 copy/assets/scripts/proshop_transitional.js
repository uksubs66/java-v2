/* 
 * ForeTees transitional js
 */

$(document).ready(function() {
    
    ftActivateElements($('body'));
    $('.ftMultiRecur').on('ft-refresh',function(){
        var el = $(this);
        var dateList = el.find('input[name="dates"]');
        var dateArray = [];
        var dates = el.children('div');
        var inputs = dates.find('input');
        inputs.removeClass('ftWarn');
        dates.each(function(i){
            var el2 = $(this);
            var el2Input = el2.find('input');
            el2.find('label>span>span').html(i+1);
            var dateVal = el2.find('input').val();
            if(dateVal){
                var dateParts = dateVal.split('/');
                if(dateParts.length == 3){
                    dateArray.push(dateParts[2]+''+dateParts[0]+''+dateParts[1]);
                }
            }
            var match = inputs.filter(function(i){return ($(this).val() == el2Input.val())});
            if(match.length > 1){
                match.addClass('ftWarn');
            }
        });
        dates.find('span.removeButton').remove();
        if(dates.length > 1){
            dates.append('<span class="removeButton">&nbsp;<button>Remove</button></span>');
            dates.find('span.removeButton button').click(function(e){
                $(this).closest('div').remove();
                setTimeout(function(){el.trigger('ft-refresh')},0);
                e.preventDefault();
            });
        }
        dates.find('input').unbind('change.ftMultiRecur').bind('change.ftMultiRecur',function(e){
            var el3 = $(this);
            setTimeout(function(){el.trigger('ft-refresh')},0);
        });
        dateList.val(dateArray.join(','));
    });
    $('.ftMultiRecur>button').click(function(e){
        var el = $(this);
        var parent = el.closest('.ftMultiRecur');
        var dates = parent.children('div');
        var tpl = dates.first().clone(false);
        var tplInput = tpl.find('input');
        tplInput.val('')
         .removeClass('hasDatepicker')
         .removeClass('ftWarn')
         .attr('id','')
         .attr('data-ftdefaultdate',dates.last().find('input').val());
        tpl.find('button').remove();
        el.before(tpl);
        ftActivateElements(tpl);
        parent.trigger('ft-refresh');
        tplInput.get(0).focus();
        e.preventDefault();
    });
    $('.ftMultiRecur').trigger('ft-refresh');
});

// Return a new date object from the integer date format YYYYMMDD and optional integer time hhmm
function ftIntDateToDate(intDate, intTime){
    var strDate = intDate.toString();
    var year = parseInt(strDate.substr(0,4), 10);
    var month = parseInt(strDate.substr(4,2), 10) - 1;
    var day = parseInt(strDate.substr(6,2), 10);
    var hour = 0;
    var min = 0;
    if(typeof intTime != "undefined"){
        var strTime = intTime.toString();
        min = parseInt(strTime.substr(2,2), 10);
        hour = parseInt(strTime.substr(0,((strTime.length == 3)?1:2)), 10);
    }
    return new Date(year, month, day, hour, min, 0, 0);
};

// Return a new date object from the string date format MM/DD/YYYY
function ftStringDateToDate(strDate){
    var date_array = strDate.split("/");
    var year = parseInt(date_array[2], 10);
    var month = parseInt(date_array[0], 10) - 1;
    var day = parseInt(date_array[1], 10);
    return new Date(year, month, day, 0, 0, 0, 0);
}

function ftActivateElements(obj){
    // Activate date-picker
    //console.log('DP check...');
    obj.find("input.ft_date_picker_field").not(".hasDatePicker").each(function(){
        //console.log('DP set...');
        var el = $(this);
        var options = {
            showOn: "both",
            changeMonth: true,
            changeYear: true,
            format: 'mm/dd/yyyy',
            buttonImage: "../assets/images/calendar_field.png",
            onSelect: function(){
                $(this).change();
            },
            showButtonPanel: true,
            closeText: 'Close',
            currentText: '',
            beforeShow:function(){
                // make sure the date picker shows over other items
                setTimeout(function(){$('.ui-datepicker').css('z-index',3000);},0);
            }
        };
        el.mask("99/99/9999");
        if(el.attr("data-ftstartdate")){
            options.minDate = ftStringDateToDate($(this).attr("data-ftstartdate"));
        }
        if(el.attr("data-ftenddate")){
            options.maxDate = ftStringDateToDate($(this).attr("data-ftenddate"));
        }
        if(el.attr("data-ftdefaultdate")){
            options.defaultDate = ftStringDateToDate($(this).attr("data-ftdefaultdate"));
        }
        el.datepicker(options);
       // el.datepicker("widget")
       //     .addClass("calendar")
       //     .addClass("aboveMenu");
        el.attr("readonly","true");
        el.change(function(){
            var dp = $(this);
            var curDate = dp.datepicker("getDate");
            var maxDate = new Date(dp.datepicker("option","maxDate"));
            var minDate = new Date(dp.datepicker("option","minDate"));
            if(isNaN(Date.parse(dp.val()))){
                if(dp.data("ftLastGoodDate")){
                    dp.datepicker("setDate", dp.data("ftLastGoodDate"));
                } else if(dp.datepicker("option","maxDate")){
                    dp.datepicker("setDate", maxDate);
                } else if(dp.datepicker("option","minDate")){
                    dp.datepicker("setDate", minDate);
                } else {
                    dp.datepicker("setDate", new Date());
                }
            }
            if (dp.datepicker("option","maxDate") && curDate > maxDate) {
                dp.datepicker("setDate", maxDate);
            }
            if (dp.datepicker("option","minDate") && curDate < minDate) {
                dp.datepicker("setDate", minDate);
            }
            dp.data("ftLastGoodDate", dp.datepicker("getDate"));
        });
    });
    // Init help icons
    //console.log('finding help buttons');
    /*
    obj.find('a.helpButton,a.helpTopic,.rwdHelpMenu a').not('.ft-helpLink').addClass('ft-helpLink').click(function(e){
        var el = $(this);
        var helpFile = el.attr('data-fthelp');
        $('#rwdNav2>a#rwdNavButton').trigger('ft-close');
        if(helpFile){
            el.foreTeesModal("help",helpFile);
            e.preventDefault();
        } else {
            helpFile = el.attr('data-fthelplink');
            var altTitle = el.attr('data-fthelptitle');
            if(helpFile){
                el.foreTeesModal("help",{fullPath:helpFile,altTitle:altTitle});
                e.preventDefault();
            }         
        }
    });
    ftInitPageHelp(obj);
    */
}

