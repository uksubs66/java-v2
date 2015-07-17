/* 
 * Read session parameters from meta tag
 */

(function($){
    
    var options = {
        zipcode:"55127"
    };
    
    var json_data = null;
    
    var methods = {
        
        get: function(option){
            if(json_data == null){
                // Get json
                var jso = $('meta[name="ft-session-parms"]');
                if(jso.length){
                    var js = jso.attr("content");
                    try {
                        var jo = JSON.parse(js);
                        json_data = jo;
                        options = $.extend(true,{}, options,jo);
                        if(options.zipcode == ""){
                            options.zipcode = "55127";
                        }
                    } catch (e) {
                    //console.log("bad session json");
                    }
                }
            }
            if(option){
                return options[option];
            }else{
                return options;
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
    
    $.fn.foreTeesSession = function ( method ){
        if (methods[method]) {
            return methods[ method ].apply (this, Array.prototype.slice.call( arguments, 1));
        } else if ( typeof method === 'object' || ! method) {
            return methods.get.apply( this, arguments );
        } else {
            $.error( 'Method ' + method + ' does not exist on jQuery.foreTeesSession' );
        }     
    }
})(jQuery);