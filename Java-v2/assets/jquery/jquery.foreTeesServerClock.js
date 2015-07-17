
/**************************************
 * 
 * Server Clock Plugin
 * 
 * ************************************
 */

(function($){
    
    var methods = {

        init: function(){

            return this.each(function(index) {

                var clock_obj = $(this);

                if(typeof(clock_obj.data('ftServerClock_is_running')) == "undefined" || clock_obj.data('ftServerClock_is_running') != true){
                    // We're the first to run
                    clock_obj.foreTeesServerClock("sync");
                }else{
                    clock_obj.foreTeesServerClock("tick");
                }

            });

        },


        sync: function(){

            $(this).each(function() {

                var clock_obj = $(this);
                var ft_club = clock_obj.attr("data-ftclub");
                if (ft_club == null || ft_club == "undefined") {
                    clock_obj.html("club missing...");
                    var postdata = {
                        appCodeName: navigator.appCodeName,
                        appName: navigator.appName,
                        cookieEnabled: navigator.cookieEnabled,
                        platform: navigator.platform,
                        userAgent: navigator.userAgent,
                        error: 'Clock is missing club name',
                        rawResponse: '',
                        page: document.URL,
                        userName: $.fn.foreTeesSession("get","user"),
                        clubName: $.fn.foreTeesSession("get","club")
                    }
                    $.ajax({
                        url: "data_logger",
                        dataType: 'text',
                        success: function(data){

                        },
                        type:'POST',
                        data:postdata,
                        error: function(){

                        }
                    });
                    return;
                }
                var url = 'clock?club='+ft_club+'&new_skin=1';
                // Check if any other server_clock plugs are running.
                $.ajax({
                    url: url,
                    context:clock_obj,
                    dataType: 'json',
                    success: function(data){
                        clock_obj = $(this);
                        var server_ms = parseInt(data.ms); // adjust to client tz
                        var d = new Date();
                        var client_ms = d.getTime();
                        var client_tz_offset = d.getTimezoneOffset() / 60;
                        var club_tz_offset = (parseInt(data.club_tz_offset) * -1);
                        var client_to_club_offset = client_tz_offset - club_tz_offset;
                        var client_to_club_offset_ms = (((client_to_club_offset * 60) * 60) * 1000);
                        var server_tz_offset = (parseInt(data.server_tz_offset) * -1);
                        var server_client_diff = client_tz_offset - server_tz_offset;
                        server_ms += (((server_client_diff * 60) * 60) * 1000)
                        var diff_ms = (server_ms - client_ms) - client_to_club_offset_ms;

                        clock_obj.data('ftServerClock_diff_ms', diff_ms);
                        clock_obj.data('ftServerClock_client_to_club_offset_ms', client_to_club_offset_ms);

                        if(clock_obj.data('ftServerClock_is_running')!=true){
                            clock_obj.data('ftServerClock_is_running', true);
                            clock_obj.foreTeesServerClock("tick");
                            // Update every 1/2 second
                            setInterval($.proxy(function(){
                                $(this).foreTeesServerClock("tick");
                            },clock_obj), 200);
                            // Sync every min.
                            setInterval($.proxy(function(){
                                $(this).foreTeesServerClock("sync");
                            },clock_obj), 60000);
                        }
                            
                    },
                    error: function(){
                        //alert("error!");
                        // Could not get time from server.
                        // Try again in 5 seconds
                        setTimeout($.proxy(function(){
                            $(this).foreTeesServerClock("sync");
                        }),$(this), 5000);
                    }
                });
            });

        },

        tick: function(){

            $(this).each(function() {

                var clock_obj = $(this);
                var diff_ms = clock_obj.data('ftServerClock_diff_ms');
                var client_to_club_offset_ms = clock_obj.data('ftServerClock_client_to_club_offset_ms');
                var clock = new Date();
                var curr_ms = clock.getTime();
                //alert(curr_ms+':'+diff_ms+':'+client_to_club_offset_ms);
                clock.setTime(curr_ms + diff_ms + client_to_club_offset_ms);
                var h=clock.getHours();
                var m=clock.getMinutes();
                var s=clock.getSeconds();
                var ampm = 'PM';
                m=(m<10)?'0'+m:m;
                s=(s<10)?'0'+s:s;
                if(h>12) {
                    h=h-12;
                } else if (h==0) {
                    h=12;
                    ampm='AM';
                } else if(h!=12) {
                    ampm='AM';
                }
                clock_obj.html(h+':'+m+':'+s+' '+ampm);


            });

        }
       
    };
       
    $.fn.foreTeesServerClock = function ( method ){
        if (methods[method]) {
            return methods[ method ].apply (this, Array.prototype.slice.call( arguments, 1));
        } else if ( typeof(method) === 'object' || ! method) {
            return methods.init.apply( this, arguments );
        } else {
            $.error( 'Method ' + method + ' does not exist on jQuery.foreTeesServerClock' );
        }     
    }

})(jQuery);

