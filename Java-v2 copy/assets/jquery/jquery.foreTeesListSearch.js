/* 
 * Add search option to select list
 */

(function($){
    
    var options = {
        
        name:"Name List",
        noSearchResults:["No results found:", "Click the clear button", "to try again"],
        noResults:[],
        noResultsPartner:['Your partner list is empty.', '', 'Select "Partners" from', 'main menu to add partners.'],
        noResultsLetter:['No members found with','last name starting in "[last_name_list_type]".', '', 'Select a different letter.'],
        clearButton:"Clear",
        instructions:"Click on name to select",
        searchPrompt:"Search List"
        
    };
    
    var methods = {
        
        init: function(new_option){

            var option = $.extend({}, options, new_option);
            
            var obj = $(this);
            var container = null;
            if(obj.parents(".element_group").length){
                container = obj.parents(".element_group").first();
            }else{
                container = obj;
            }
            
            var slotParms = option.slotParms;
            if(typeof slotParms != "object"){
                slotParms = {
                    last_name_list_type:"none"
                };
            }
            var emptyListPrompt = option.noResults;
            //console.log("test:"+slotParms.last_name_list_type);
            if(slotParms.last_name_list_type == "partners"){
                emptyListPrompt = option.noResultsPartner;
            } else if(slotParms.last_name_list_type.length == 1){
                emptyListPrompt = option.noResultsLetter;
            }
            
            // Mobile browsers don't like select lists with a size attribute
            if(ftIsMobile()){
                obj.removeAttr("size");
            }
            obj.prop("selected", false);
            
            if(typeof(obj.data("ft-searchList")) != "undefined"){
                // remove old search list, if it exists
                obj.data("ft-searchList").remove();
            }
            
            obj.unbind('change.ft-listSearch');
            obj.bind('change.ft-listSearch',option.change);
            
            // clone the select box for searching
            var selectBase = obj.clone(true,true);
            selectBase.hide(); // hide the cloned select box

            var html = "";
            html += '<div class="list_search"><h3>'+option.name+'</h3>';
            html += '<div><a href="#" class="standard_button">'+option.clearButton+'</a><div><input type="text"></div>';
            html += '<div style="clear:both;"></div></div>';
            html += '<p class="small_text">'+option.instructions+'</p></div>';

            var listSearchObj = $(html);
            container.before(listSearchObj);

            var listSearchBox = listSearchObj.find('input').first();
            var clearSearchBox = listSearchObj.find('a').first();                       
            
            $("body").append(selectBase);
            
            obj.data("ft-searchList", selectBase);
            obj.data("ft-listSearchObj", listSearchObj);
            
            clearSearchBox.data("ft-searchList", obj);
            clearSearchBox.data("ft-searchBox", listSearchBox);
            clearSearchBox.click(function(){
                var searchBox = clearSearchBox.data("ft-searchBox");
                searchBox.val("");
                searchBox.keyup();
                searchBox.blur();
                return false;
            });
                            
            // Initialize searchbox
            listSearchBox.data("ft-searchResults", obj);
            listSearchBox.data("ft-searchList", selectBase);
            listSearchBox.data("ft-searchPrompt", option.searchPrompt);
            listSearchBox.val(option.searchText);
            listSearchBox.focus(function(){
                if($(this).val() == $(this).data("ft-searchPrompt")){
                    $(this).val("");
                }
            });
            listSearchBox.blur(function(){
                if($.trim($(this).val()) == ""){
                    $(this).val($(this).data("ft-searchPrompt"));
                }
            });
                            
            listSearchBox.keyup(function(){
                var searchList = $(this).data("ft-searchList");
                var searchResults = $(this).data("ft-searchResults");
                var searchVal = $(this).val().toLowerCase();
                var searchArray = searchVal.split(/[, ]+/i);
                var foundValue = false;
                searchResults.find("option").remove();
                searchList.find("option").each(function(){
                    $(this).prop("selected", false);
                    /*
                    if($(this).attr("value").toLowerCase().indexOf(searchVal,0) > -1){
                        searchResults.append($(this).clone(true,true));
                    }
                 */
                    if(ftFindAllInString(searchArray, $(this).attr("value")) || ftFindAllInString(searchArray, $(this).html())){
                        searchResults.append($(this).clone(true,true));
                    }
                });
                if(!foundValue && (searchVal != "")){
                    $(this).addClass("not_found");
                }else{
                    $(this).removeClass("not_found");
                }
                if((searchResults.find("option").length < 1) && (searchList.find("option").length > 0)){
                    for(var i = 0; i < option.noSearchResults.length; i++){
                        searchResults.append('<option>'+option.noSearchResults[i]+'</option>');
                    }
                }else if (searchList.find("option").length < 1){
                    for(var i = 0; i < emptyListPrompt.length; i++){
                        searchResults.append('<option>'+ftReplaceKeyInString(emptyListPrompt[i], slotParms)+'</option>');
                    }
                }
                searchResults.find("option:selected").prop("selected",false);   
            });
                            
            // Force a search reset
            clearSearchBox.click();
 
        },
        
        clear: function(){
            if(typeof($(this).data("ft-searchList")) != "undefined"){
                $(this).data("ft-searchList").remove();
                $(this).data("ft-listSearchObj").remove();
            }
        },
       
        setOptions: function ( option ) {
            
            if(typeof option == "object"){
                //// console.log("setting options");
                $.extend(true, options,option);
            }
            
        },
       
        getOptions: function () {
            
            return options;
            
        }
    };
    
    $.fn.foreTeesListSearch = function ( method ){
        if (methods[method]) {
            return methods[ method ].apply (this, Array.prototype.slice.call( arguments, 1));
        } else if ( typeof method === 'object' || ! method) {
            return methods.init.apply( this, arguments );
        } else {
            $.error( 'Method ' + method + ' does not exist on jQuery.foreTeesListSearch' );
        }     
    }
})(jQuery);
