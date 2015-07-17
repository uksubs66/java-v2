/*************************************************************
 *
 * foreTeesMemberSearch Plugin
 * 
 * Used for seaching members and guests
 *  
 *************************************************************/

(function($){ 

    var isMP = $.fn.foreTeesSession("get","activity_id") == 9000, partnerLower = (isMP?'VIP':'partner'), partnerProper = (isMP?'VIP':'Partner');
    
    var c = { // constants
        selectedItem: 'ftmsSelectedItem'
    };
    var options = {
        text: {
            tbdText: "To Be Decided (TBD)",
            prompt: "Enter member name",
            partnerSelectTab: partnerProper+"s",
            memberSearchTab: "Members",
            guestTypesTab: "Guests",
            guestTbdTab: "TBD",
            guestDbTab: "Search Guests",
            newGuestTab: "New Guest",
            guestTbdPrompt: 'Select "X" to mark as "To Be Decided".',
            disabledGuestType: "Some guest types may be disabled for this time.",
            emptyPartnerList: "<p>Your "+partnerLower+" list is empty.<p><p>You can manage "+partnerLower+"s from the \""+partnerProper+"s\" menu above.</p>",
            emptyPartnerListEmail: "<p>Your partner list is empty, or your selected "+partnerLower+"s do not have valid email addresses.<p><p>You can manage "+partnerLower+"s from the \""+partnerProper+"s\" menu above, but only "+partnerLower+"s having a valid email address will show in this list.</p>",
            searchInstructions:"Enter all or part of the member's name to begin search.",
            searchMemberNameNumberInstructions:"Enter all or part of the member's name or number to begin search.",
            guestSearchInstructions:"Enter all or part of the guest's name to narrow this list.",
            guestSearchTbaInstructions:'Select "TBA" if you do not yet know the name of your guest.'
        },
        onMemberSelect: function(record,el,o){},
        onPartnerSelect: function(record,el,o){},
        onGuestSelect: function(record,el,o){},
        onGuestDbSelect: function(record,el,o){},
        onTbdSelect: function(record,el,o){},
        validateMembers: function(className,opt,o){},
        onSelect: function(record,el,o){},
        onAfterSelect: function(className,opt,o){},
        selectedUsers: [],
        searchDelay:500,
        partnerLookup:{},
        memberLookup:{},
        partnerList:{},
        requireGhin:false,
        requireEmail:false,
        showGender:false,
        showMtypeStat:false,
        filterMship:false,
        beforeSelect: function(record,el,o){
            if(!record.display){
                // Build display name
                record.display = record.first + " " + ((record.middle != "")?record.middle+" ":"") + record.last;
            }
            el.children("span").addClass("selectHighlight");
            el.children("span").removeClass("selectHighlight",500);
        },
        onKeydown: function(e,el,o) {
            //var opts = methods.getOptions(o);
            var current = el.find('.ftMs-resultList .selected').first();
            var list = el.find('.ftMs-resultList');
            var prev = current.prev('.ftMs-listItem');
            if(!prev.length){
                prev = current;
            }
            var next = current.next('.ftMs-listItem');
            if(!next.length){
                next = current;
            }
            if(e.which == $.ui.keyCode.UP){
                current.removeClass('selected');
                prev.addClass('selected');
                list.scrollTop(list.scrollTop() + (prev.position().top - list.position().top) - (list.height()/2) + (prev.height()/2) );
                el.data(c.selectedItem,prev);
                e.preventDefault();
            } else if(e.which == $.ui.keyCode.DOWN){
                current.removeClass('selected');
                next.addClass('selected');
                list.scrollTop(list.scrollTop() + (next.position().top - list.position().top) - (list.height()/2) + (next.height()/2) );
                el.data(c.selectedItem,next);
                e.preventDefault();
            } else if(e.which == $.ui.keyCode.ENTER || e.which == $.ui.keyCode.TAB){
                // Click selected item
                if(el.data(c.selectedItem)){
                    el.data(c.selectedItem).click();
                }
            }
        }
    };
    
    function _toTitleCase(str){
        return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
    }
    
    var classCache = {};
    
    function _getClassFromWords(words, class_prefix){
        var i, cacheKey = words+'::'+class_prefix, result = classCache[cacheKey];
        if(result){
            return result;
        } else {
            result = '';
        }
        var word, array = words.trim().split(/[^a-zA-Z0-9]+/), length = array.length;
        for(i = 0; i < length; i++){
            word = _toTitleCase(array[i]);
            if(i == 0){
                result += ' ' + class_prefix + 'St-' + word;
                result += ' ' + class_prefix + '-' + word;
            } else {
                result += word;
            }
            if(i+1 == length){
                result +=  ' ' + class_prefix + 'En-' + word;
            }
        }
        classCache[cacheKey] = result;
        return result;
    }
    
    
    var methods = {
        
        init: function(opt){
            
            var o = $(this);
            if(!o.length){
                return;
            }
            
            var opts = methods.setOptions(opt,o);
            var container = $('<div class="ftMs-memberSelect"></div>');
            var partnerSelectTab = $('<li><div data-ftTab=".ftMs-partnerSelect">'+opts.text.partnerSelectTab+'</div></li>');
            var memberSearchTab = $('<li><div data-ftTab=".ftMs-memberSearch" data-ftFocus=".ftMs-input">'+opts.text.memberSearchTab+'</div></li>');
            var guestTypesTab = $('<li><div data-ftTab=".ftMs-guestTypes">'+opts.text.guestTypesTab+'</div></li>');
            var guestTbdTab = $('<li><div data-ftTab=".ftMs-guestTbd">'+opts.text.guestTbdTab+'</div></li>');
            
            var partnerCount = 0;
            var activeTab = $('');
            
            var tabs = $('<ul class=\"ftMs-tabs\"></ul>');
            var dn = 'ftMs-record';
            
            var inUseClass = "ftMs-nameInUse";

            container.append(tabs);
            
            function _getSelectedNames(){
                
                var sn = opt.selectedNames;
                
                if(typeof sn == "object"){
                    return sn;
                } else if(typeof sn == "function"){
                    return sn();
                } else {
                    return false;
                }

            }
            
            if(opts.guestDb){    
                // Create Guest Database Search Tab
                var guestDbCon = $('<div class="ftMs-block ftMs-guestDbSelect"></div>');
                var guestSearchCon = $('<div class="ftMs-search"></div>');
                var guestSearchBox = $('<input type="text" class="ftMs-input" value=""/>');
                var guestDbResults = $('<div class="ftMs-results"></div>');
                var guestSearchClear = $('<span class="ftMs-clear"></span>');

                //searchResults.append('<div class="ftMs-resultList"><div class=\"ftMs-resultMessage\"><p><span>'+opts.text.searchInstructions+'</span></p></div></div>');
                guestDbResults.append('<div class="ftMs-overlay"></div>');

                guestSearchClear.click(function(){
                    guestSearchBox.val("");
                    guestSearchBox.trigger("propertychange");
                });
                guestSearchCon.append(guestSearchClear);
                guestSearchCon.append(guestSearchBox);
                guestDbResults.append('<div class="ftMs-overlay"></div>');
                guestDbCon.append(guestSearchCon).append(guestDbResults);

                container.append(guestDbCon);
                guestDbResults.data(c.selectedItem, false);
                
                // No tabs for guest db, yet
                
                // Load guest db data
                var url = "Common_guestdb?json_mode=true&modal";
                
                //opts.guestDb = data;
                var fullDb = [];
                var results = $('<div class="ftMs-resultList"></div>');

                // Generate lookup tables/index for first and last name
                var lLookup = {};
                var fLookup = {};
                
                var useTBA = false;
                //console.log(opts.guestDbList);
                for (var key in opts.guestDbList) {
                    
                    var record = opts.guestDbList[key];
                    
                    var lastLower = record.last.toLowerCase();
                    var firstLower = record.first.toLowerCase();
                    //console.log(fullDb);
                    record.firstLower = firstLower;
                    record.lastLower = lastLower;

                    var fKey = firstLower[0];
                    if(fKey){
                        if(!fLookup[fKey]){
                            fLookup[fKey] = {};
                        }

                        if(!fLookup[fKey][firstLower]){
                            fLookup[fKey][firstLower] = [];
                        }

                        fLookup[fKey][firstLower].push(record);
                    }
                    var lKey = lastLower[0];
                    if(lKey){
                        if(!lLookup[lKey]){
                            lLookup[lKey] = {};
                        }

                        if(!lLookup[lKey][lastLower]){
                            lLookup[lKey][lastLower] = [];
                        }
                        lLookup[lKey][lastLower].push(record);
                    } else if(record.guest_name.toLowerCase() == "tba") {
                        useTBA = true;
                    }
                    fullDb.push(record);
                }

                // Make search box work

                guestSearchBox.bind('keydown',function(e){
                    var opts = methods.getOptions(o);
                    opts.onKeydown(e,guestDbResults,o);
                });
                var lastVal = " ";
                guestSearchBox.bind("input propertychange keyup change", function(e){
                    
                    var sb = $(this);
                    // If it's the propertychange event, make sure it's the value that changed.
                    if (window.event && event.type == "propertychange" && event.propertyName != "value"){
                        return;
                    }
                    if(sb.val() == lastVal){
                        return;
                    }
                    lastVal = sb.val();
                    guestDbResults.addClass("inUse");
                    var sdelay = 300;
                    if(sb.val()==""){
                        sdelay = 1; // if empty search, search right away
                    }

                    // Clear any previously set timer before setting a fresh one
                    window.clearTimeout(sb.data("ftmsTimeout"));
                    // Take action if user hasn't typed for 1/2 second
                    sb.data("ftmsTimeout", setTimeout(function(){
                        // Search index
                        var nameMatch = [];
                        var st = false;
                        var d = new Date();

                        if($.trim(sb.val())==""){
                            // Empty search - return all results
                            //console.log('display all');
                            nameMatch = fullDb;
                        } else {
                            // See what we can search for
                             st = methods.parseName(sb.val());
                            // try last name match
                            nameMatch = methods.searchIndex(st.last,lLookup,st.exact_last);
                            if(st.first.length){
                                // Filter last name results by first name
                                for(var i = 0; i < nameMatch.length; i++){
                                    if(nameMatch[i].firstLower.indexOf(st.first.toLowerCase()) != 0){
                                        nameMatch.splice(i,1);
                                        i--;
                                    }
                                }
                            }
                            if(!st.first.length){
                                // If only one part of a name was entered, search for first name too, using last name
                                nameMatch = methods.searchIndex(st.last,fLookup,false,nameMatch);
                            }
                        }

                        //console.log('found:'+data.found);
                        var opts = methods.getOptions(o);
                        //opts.memberLookup = {};
                        guestDbResults.data(c.selectedItem, false);
                        guestDbResults.removeClass("inUse");
                        guestDbResults.children(':not(.ftMs-overlay)').remove();
                        var results = $('<div class="ftMs-resultList"></div>');

                        if(!st && ((useTBA && nameMatch.length > 1) || (!useTBA && nameMatch)) ){
                            results.append('<div class=\"ftMs-resultMessage\"><p><span>'+opts.text.guestSearchInstructions+'</span></p></div>');
                        }
                        if (!st && useTBA){
                            //results.append('<div class=\"ftMs-resultMessage\"><p><span>'+opts.text.guestSearchTbaInstructions+'</span></p></div>');
                        }
                        //var count = 0;
                        var dedupe = {};
                        for (var k = 0; k < nameMatch.length; k++ ) {
                            var record = nameMatch[k];
                            if(!dedupe[record.guest_id]){
                                dedupe[record.guest_id] = true;

                                // Try to highlight what we searched for
                                var lastName = record.last;
                                var firstName = record.first;

                                if(st){
                                    if(st.last.length){
                                        i = record.lastLower.indexOf(st.last.toLowerCase());
                                        if(i==0){
                                            lastName = '<b>'+record.last.substr(0,st.last.length)+'</b>'+record.last.substr(st.last.length);
                                        }
                                    }
                                    var firstSearch = st.first;
                                    if(!firstSearch.length){
                                        firstSearch= st.last;
                                    }
                                    if(firstSearch.length){
                                        i = record.firstLower.indexOf(firstSearch.toLowerCase());
                                        if(i==0){
                                            firstName = '<b>'+record.first.substr(0,firstSearch.length)+'</b>'+record.first.substr(firstSearch.length);
                                        }
                                    }
                                }
                                // Output the data
                                var name = lastName + ', ' + firstName + ' ' + record.middle;
                                if(!lastName){
                                    name = record.display_name;
                                }
                                var result = $('<div class=\"ftMs-listItem\"><span>'+name+'</span></div>');
                                result.data(dn,record);
                                result.click(function(e){
                                    var el = $(this);
                                    var opts = methods.getOptions(o);
                                    if(guestDbResults.data(c.selectedItem)){
                                        guestDbResults.data(c.selectedItem).removeClass('selected');
                                    }
                                    guestDbResults.data(c.selectedItem,el);
                                    el.addClass('selected');
                                    opts.beforeSelect(el.data(dn),el,o);
                                    opts.onSelect(el.data(dn),el,o);
                                    opts.onGuestDbSelect(el.data(dn),el,o);
                                    opts.onAfterSelect(el.data(dn),el,o);
                                });
                                if(!guestDbResults.data(c.selectedItem) && st){
                                    result.addClass('selected');
                                    guestDbResults.data(c.selectedItem, result);
                                }
                                results.append(result);
                                
                                //count ++;
                            }
                        }
                        guestDbResults.append(results);
                        var errorMessage = "";

                        if(!fullDb.length){
                            errorMessage = "No guests in database.  You must first add new guests.";
                        } else if(!nameMatch){
                            if(!st.first.length){
                                errorMessage = 'No results found having a first or last name beginning with "'+st.last+'".';
                            } else if (st.exact_last){
                                errorMessage = 'No results found having a last of "'+st.last+'" and a first name beginning with "'+st.first+'".';
                            } else {
                                errorMessage = 'No results found having a last name beginning with "'+st.last+'" and a first name beginning with "'+st.first+'".';
                            }
                        }
                        if(errorMessage.length){
                            results.append('<div class="ftMs-resultMessage ftMs-empty"><p><span>'+errorMessage+'</span></p><div>');
                        }

                    }, sdelay));
                });
                // Force an initial search
                guestSearchBox.trigger("propertychange");
            }

            if(opts.partnerSelect){    
                // Create Partner List
                
                var partnerCon = $('<div class="ftMs-block ftMs-partnerSelect"></div>');
                var partnerResults = $('<div class="ftMs-results"></div>');
                partnerResults.append('<div class="ftMs-overlay"></div>');
                partnerCon.append(partnerResults);
                container.append(partnerCon);
                partnerResults.data(c.selectedItem, false);
                
                tabs.append(partnerSelectTab);
                activeTab = partnerSelectTab;
                
                var url = "data_loader?json_mode=true&name_list=true&letter=partners"+(opts.requireEmail?"&email=true":"");
                partnerResults.addClass("inUse");
                // Get partner list
                $.ajax({
                    url: url,
                    dataType: 'json',
                    success: function(data){
                        
                        partnerResults.removeClass("inUse");
                        opts.partnerList = data;
                        opts.partnerLookup = {};
                        
                        var results = $('<div class="ftMs-resultList"></div>');
                        
                        //var selectedNames = _getSelectedNames();

                        for (var key in data) {
                            var record = data[key];
                            var name = record["name"];
                            if(opts.showGhin){
                                name += " <span class=\"ftMs-ghin\">" + record.ghin + "</span>";
                            }
                            var result = $('<div class=\"ftMs-listItem\"><span>'+name+'</span></div>');
                            if(opts.selectedUsers && record.username && opts.selectedUsers.indexOf(record.username) > -1){
                                result.addClass(inUseClass);
                            } //else if(selectedNames) {
                                
                            //}
                            result.data(dn,record);
                            result.click(function(e){
                                var el = $(this);
                                var opts = methods.getOptions(o);
                                if(partnerResults.data(c.selectedItem)){
                                    partnerResults.data(c.selectedItem).removeClass('selected');
                                }
                                partnerResults.data(c.selectedItem,el);
                                el.addClass('selected');
                                opts.beforeSelect(el.data(dn),el,o);
                                opts.onSelect(el.data(dn),el,o);
                                opts.onPartnerSelect(el.data(dn),el,o);
                                opts.onAfterSelect(el.data(dn),el,o);
                                if(opts.selectedUsers && el.data(dn).username && opts.selectedUsers.indexOf(el.data(dn).username) > -1){
                                    el.addClass(inUseClass);
                                } else {
                                    el.removeClass(inUseClass);
                                }
                            });
                            results.append(result);
                            opts.partnerLookup["user_"+record.username.toLowerCase()] = result;
                            partnerCount ++;
                        }
                        if(partnerCount < 1){
                            results.append('<div class=\"ftMs-resultMessage ftMs-empty\"><p><span>'+(opts.requireEmail?opts.text.emptyPartnerListEmail:opts.text.emptyPartnerList)+'</span></p></div>');
                        }
                        partnerResults.append(results);
                        
                    },
                    error: function(xhr){
                        partnerResults.removeClass("inUse");
                        partnerResults.children(':not(.ftMs-overlay)').remove();
                        partnerResults.append('<div class="ftMs-instructions ftMs-error">'+ftMessage.ajaxError+'</div>');
                        ft_logError("Error performing name_search", xhr);
                    }
                });
                o.on("ftMs-update",function(){
                    partnerResults.find(".ftMs-listItem").removeClass(inUseClass);
                    for(var i in opts.selectedUsers){
                        var user = opts.selectedUsers[i].toLowerCase();
                        var el = opts.partnerLookup["user_"+user];
                        if(user && opts.partnerLookup["user_"+user]){
                            el.addClass(inUseClass);
                        }
                    }
                });
            }
            
            if(opts.memberSearch){
                // Create member search widget

                var memberContainer = $('<div class="ftMs-block ftMs-memberSearch"></div>');
                var searchCon = $('<div class="ftMs-search"></div>');
                var searchBox = $('<input type="text" class="ftMs-input" value=""/>');
                var searchResults = $('<div class="ftMs-results"></div>');
                var searchClear = $('<span class="ftMs-clear"></span>');
                var memSearchInst = $('<div class="ftMs-resultList"><div class=\"ftMs-resultMessage\"><p><span>'+(opts.searchByMemberNumber?opts.text.searchMemberNameNumberInstructions:opts.text.searchInstructions)+'</span></p></div></div>');
                memSearchInst.find('.ftMs-resultMessage').click(function(e){searchBox.focus();e.preventDefault();});
                searchResults.append(memSearchInst.clone(true));
                tabs.append(memberSearchTab);
                if(!opt.partnerSelect){
                    activeTab = memberSearchTab;
                }
                searchClear.click(function(e){
                    searchBox.val("");
                    searchBox.trigger("propertychange");
                    searchBox.focus();
                    e.preventDefault();
                });
                searchCon.append(searchClear);
                searchCon.append(searchBox);
                searchResults.append('<div class="ftMs-overlay"></div>');
                memberContainer.append(searchCon).append(searchResults);
                container.append(memberContainer);
                
                if(opts.filterMship){
                    // Query server to see if we have any memtypes to use
                    ftapi.get([
                        {
                            command:'membershipTypes',
                            activity_id:$.fn.foreTeesSession('get','activity_id'),
                            success:function(data){
                                if(data.length > 1){
                                    // Only display filter selection if more than one type
                                    var ii, item, 
                                        optionHtml = '<option value="">Membership Type: ANY</option>';
                                    for(ii = 0; ii < data.length; ii++){
                                        item = data[ii];
                                        optionHtml += '<option value="'+item.name+'">'+item.name+'</option>';
                                    }
                                    var $mshipSelectContainer = $('<div class="ftMs-filter ftMs-mshipFilter"><select>'+optionHtml+'</select></div>');
                                    $mshipSelectContainer.find('select').change(function(){searchBox.change();});
                                    searchResults.before($mshipSelectContainer);
                                }
                            }
                        },
                        {
                            command:'memberTypes',
                            activity_id:$.fn.foreTeesSession('get','activity_id'),
                            success:function(data){
                                if(data.length > 1){
                                    // Only display filter selection if more than one type
                                    var ii, item, 
                                        optionHtml = '<option value="">Member Type: ANY</option>';
                                    for(ii = 0; ii < data.length; ii++){
                                        item = data[ii];
                                        optionHtml += '<option value="'+item.name+'">'+item.name+'</option>';
                                    }
                                    var $mshipSelectContainer = $('<div class="ftMs-filter ftMs-mtypeFilter"><select>'+optionHtml+'</select></div>');
                                    $mshipSelectContainer.find('select').change(function(){searchBox.change();});
                                    searchResults.before($mshipSelectContainer);
                                }
                            }
                        }
                    ]);
                }
                
                searchBox.bind('keydown',function(e){
                    var opts = methods.getOptions(o);
                    opts.onKeydown(e,searchResults,o);
                });
                var lastVal = " ", lastMshipFilter = '', lastMtypeFilter = '';
                searchBox.bind("input propertychange keyup change", function(e){
                    var sb = $(this), 
                        mshipFilterVal = memberContainer.find('.ftMs-mshipFilter select option:selected').val(),
                        mtypeFilterVal = memberContainer.find('.ftMs-mtypeFilter select option:selected').val();
                    // If it's the propertychange event, make sure it's the value that changed.
                    if (mtypeFilterVal == lastMtypeFilter && mshipFilterVal == lastMshipFilter && window.event && window.event.type == "propertychange" && window.event.propertyName != "value"){
                        //console.log(e.propertyName);
                        return;
                    }
                    if(sb.val() == lastVal && mshipFilterVal == lastMshipFilter && mtypeFilterVal == lastMtypeFilter){
                        return;
                    }
                    searchResults.addClass("inUse");
                    lastVal = sb.val();
                    lastMshipFilter = mshipFilterVal;
                    lastMtypeFilter = mtypeFilterVal;
                    if($.trim(sb.val()) == ""){
                        searchResults.children(':not(.ftMs-overlay)').remove();
                        searchResults.append(memSearchInst.clone(true));
                        window.clearTimeout(sb.data("ftmsTimeout"));
                        searchResults.removeClass("inUse");
                        return;
                    }
                    // Clear any previously set timer before setting a fresh one
                    window.clearTimeout(sb.data("ftmsTimeout"));
                    // Take action if user hasn't typed for 1/2 second
                    sb.data("ftmsTimeout", setTimeout(function(){
                        // Run a search
                        var searchData = {'name_search':sb.val(),
                                'limit':100};
                        if(opts.requireGhin){
                            searchData.ghin = true;
                        }
                        if(opts.requireEmail){
                            searchData.email = true;
                        }
                        if(opts.searchByMemberNumber){
                            searchData.search_member_number = true;
                        }
                        if(mshipFilterVal){
                            searchData.mship_filter = mshipFilterVal;
                        }
                        if(mtypeFilterVal){
                            searchData.mtype_filter = mtypeFilterVal;
                        }
                        
                        $.ajax({
                            type: 'GET',
                            url: "data_loader",
                            data: searchData,
                            dataType: 'json',
                            success: function(data){
                                //console.log('found:'+data.found);
                                var opts = methods.getOptions(o);
                                opts.memberLookup = {};
                                searchResults.data(c.selectedItem, false);
                                searchResults.removeClass("inUse");
                                //console.log(data);
                                searchResults.children(':not(.ftMs-overlay)').remove();
                                var messageClass = [];
                                var results = $('<div class="ftMs-resultList"></div>');
                                if(!data.returned){
                                    messageClass.push("ftMs-empty");
                                } else {
                                    //var count = 0;
                                    var hasGender = false, hasMtypeAbrv = false;
                                    for (var key in data.results) {
                                        var record = data.results[key],
                                            li, mt, ar,
                                            memClass = '', 
                                            mtype_abrv = record.mtype_status_abrv,
                                            mtype_gender = record.mtype_gender,
                                            name = record.last_match + ', ' + record.first_match + ' ' + record.middle;
                                        if(opts.showGhin){
                                            name += " <span class=\"ftMs-ghin\">" + record.ghin + "</span>";
                                        }
                                        if(opts.showGender){
                                            if(mtype_gender){
                                                hasGender = true;
                                            }
                                            name += " <span class=\"ftMs-gender\"><b>" + (mtype_gender?mtype_gender:'?') + "</b></span>";
                                        }
                                        if(opts.showMtypeStat){
                                            if(mtype_abrv){
                                                hasMtypeAbrv = true;
                                            }
                                            name += " <span class=\"ftMs-mtypeAbrv\"><b>" + (mtype_abrv?mtype_abrv:'?') + "</b></span>";
                                        }
                                        if(opts.searchByMemberNumber){
                                            name += " <span class=\"ftMs-memNum\"><span>" + record.memnum_match + "</span></span>";
                                        }
                                        
                                        
                                        memClass += _getClassFromWords(record.m_type, 'ftMtyp');
                                        memClass += _getClassFromWords(record.m_ship, 'ftMshp');
                                        if(mtype_gender){
                                            memClass += ' ftGndr'+(mtype_gender?mtype_gender:'');
                                        }
                                        if(mtype_abrv){
                                            memClass += ' ftMtyp'+(mtype_abrv?mtype_abrv:'');
                                        }
                                        var result = $('<div class=\"ftMs-listItem '+memClass+'\"><span>'+name+'</span></div>');
                                        if(opts.selectedUsers && record.username && opts.selectedUsers.indexOf(record.username) > -1){
                                            result.addClass(inUseClass);
                                        }
                                        result.data(dn,record);
                                        result.click(function(e){
                                            var el = $(this), 
                                                selectedItem =  searchResults.data(c.selectedItem),
                                                opts = methods.getOptions(o);
                                            if(selectedItem){
                                                selectedItem.removeClass('selected');
                                            }
                                            searchResults.data(c.selectedItem,el);
                                            el.addClass('selected');
                                            opts.beforeSelect(el.data(dn),el,o);
                                            opts.onSelect(el.data(dn),el,o);
                                            opts.onMemberSelect(el.data(dn),el,o);
                                            opts.onAfterSelect(el.data(dn),el,o);
                                            if(opts.selectedUsers && el.data(dn).username && opts.selectedUsers.indexOf(el.data(dn).username) > -1){
                                                el.addClass(inUseClass);
                                            } else {
                                                el.removeClass(inUseClass);
                                            }
                                            //searchBox.focus(); // We can't do this on devices with soft keyboard (ios, android etc.)
                                        });
                                        if(!searchResults.data(c.selectedItem)){
                                            result.addClass('selected');
                                            searchResults.data(c.selectedItem, result);
                                        }
                                        opts.memberLookup['user_'+record.username.toLowerCase()] = result;
                                        results.append(result);
                                        //count ++;
                                    }
                                    results.addClass((hasMtypeAbrv?"ftMs-hasMtypeAbrv":"ftMs-noMtypeAbrv"));
                                    results.addClass((hasGender?"ftMs-hasGender":"ftMs-noGender"));
                                    if(data.returned < data.found){
                                        messageClass.push("ftMs-overflow");
                                    }
                                }
                                opts.validateMembers("inSlot",opts,o);
                                searchResults.append(results);
                                if(data.messages){
                                    var message = '<div class="ftMs-resultMessage '+messageClass.join(" ")+'">';
                                    for(var i = 0; i < data.messages.length; i++){
                                        message += '<p><span>'+data.messages[i]+'</span></p>';
                                    }
                                    message += '<div>';
                                    results.append(message);
                                }
                            },
                            error: function(xhr, e_text, e){
                                searchResults.removeClass("inUse");
                                searchResults.children(':not(.ftMs-overlay)').remove();
                                searchResults.append('<div class="ftMs-instructions ftMs-error">'+ftMessage.ajaxError+'</div>');
                                ft_logError("Error performing name_search", xhr);
                                //console.log(e_text);
                            }
                        });  
                    }, opts.searchDelay));
                });
                o.on("ftMs-update",function(){
                    searchResults.find(".ftMs-listItem."+inUseClass).removeClass(inUseClass);
                    for(var i in opts.selectedUsers){
                        var user = opts.selectedUsers[i].toLowerCase();
                        var el = opts.memberLookup["user_"+user];
                        if(user && opts.memberLookup["user_"+user]){
                            el.addClass(inUseClass);
                        }
                    }
                });
            }
            
            var results, record, result;
            
            if(!$.isEmptyObject(opt.guestTypes)){
                // Create guest list
                tabs.append(guestTypesTab);
                var guestCon = $('<div class="ftMs-block ftMs-guestTypes"></div>');
                var guestResults = $('<div class="ftMs-results"></div>');
                
                if(!opt.partnerSelect && !opt.memberSearch){
                    activeTab = guestTypesTab;
                }
                
                guestCon.append(guestResults);
                container.append(guestCon);
                var hasDisabled = false;
                guestResults.data(c.selectedItem, false);
 
                var results = $('<div class="ftMs-resultList"></div>');

                for (var key in opt.guestTypes) {
                    var record = opt.guestTypes[key];
                    var name = record["guest_type"];
                    if(name.toLowerCase() == 'tbd'){
                        name = opts.text.tbdText;
                        record.readOnly = true;
                    }
                    var result = $('<div class=\"ftMs-listItem\"><span>'+name+'</span></div>');
                    result.data(dn,record);
                    //opts.guestLookup['guest_'+record.username] = result;
                    if(record.guest_type_disabled){
                        result.addClass('disabled');
                        hasDisabled = true;
                    } else {
                        result.click(function(e){
                            var el = $(this);
                            var opts = methods.getOptions(o);
                            if(guestResults.data(c.selectedItem)){
                                guestResults.data(c.selectedItem).removeClass('selected');
                            }
                            guestResults.data(c.selectedItem,el);
                            el.addClass('selected');
                            opts.beforeSelect(el.data(dn),el,o);
                            opts.onSelect(el.data(dn),el,o);
                            opts.onGuestSelect(el.data(dn),el,o);
                            opts.onAfterSelect(el.data(dn),el,o);
                        });
                    }
                    results.append(result);
                }
                if(hasDisabled){
                    results.append('<div class=\"ftMs-resultMessage ftMs-disabledNotice\"><p><span>'+opts.text.disabledGuestType+'</span></p></div>');
                }
                guestResults.append(results);
            }
            
            if(opts.guestTbd){
                // Add Member TBD (To Be Decided) option
                tabs.append(guestTbdTab);
                var tbdCon = $('<div class="ftMs-block ftMs-guestTbd"></div>');
                var tbdResults = $('<div class="ftMs-results"></div>');
                
                tbdCon.append(tbdResults);
                container.append(tbdCon);
                
                if(!opt.partnerSelect && !opt.memberSearch && $.isEmptyObject(opt.guestTypes)){
                    activeTab = guestTbdTab;
                }
                
                results = $('<div class="ftMs-resultList"></div>');
                
                record = {
                    guest_type:'X'
                };
                result = $('<div class=\"ftMs-listItem\"><span>X</span></div>');
                result.data(dn,record);
                result.click(function(e){
                    var el = $(this);
                    var opts = methods.getOptions(o);
                    if(tbdResults.data(c.selectedItem)){
                        tbdResults.data(c.selectedItem).removeClass('selected');
                    }
                    tbdResults.data(c.selectedItem,el);
                    el.addClass('selected');
                    opts.beforeSelect(el.data(dn),el,o);
                    opts.onSelect(el.data(dn),el,o);
                    opts.onTbdSelect(el.data(dn),el,o);
                    opts.onAfterSelect(el.data(dn),el,o);
                });
                results.append('<div class=\"ftMs-resultMessage ftMs-guestTbdPrompt\"><p><span>'+opts.text.guestTbdPrompt+'</span></p></div>');
                results.append(result);
                
                tbdResults.append(results);
            }
            
            o.append(container);
            
            var ac = "active";
            
            tabs.find('li>div').click(function(e){
                var el = $(this);
                tabs.find('li>div').removeClass(ac);
                el.addClass(ac);
                container.find('.ftMs-block').removeClass(ac);
                container.find(el.attr('data-ftTab')).addClass(ac);
                var focusSel = el.attr('data-ftFocus');
                if(focusSel){
                    container.find(el.attr('data-ftTab')).find(focusSel).focus();
                }
                e.preventDefault();
            });
            
            activeTab.find('div').first().click();
            // If only 1 tab, remove it
            var firstFocus;
            if(tabs.find('li').length < 2){
                firstFocus = tabs.find('li>div').first().attr('data-ftFocus');
                tabs.empty();
            }
            // If no tabs, activate first block
            if(!tabs.find('li').length){
                var firstEl = container.find('.ftMs-block').first();
                firstEl.addClass(ac);
                container.addClass('ftMs-noTabs');
                if(firstFocus){
                    var focalEl = firstEl.find(firstFocus);
                    if(focalEl.length){
                        o.data("focusOnShow",focalEl);
                        methods.focus(o);
                    }
                }
            }
            
        },
        
        focus: function(obj){
            if(!obj){
                obj = $(this);
            }
            var focusEl = obj.data("focusOnShow");
            if(focusEl){
               focusEl.focus(); 
            }
        },
        
        searchIndex: function (search, ind, exact, results) {
            if(!results){
                results = [];
            }
            search = search.toLowerCase();
            if(search.length){
                var lKey = search[0];
                if(ind[lKey]){
                    if(exact && ind[lKey][search]){
                        for(var i = 0; i < ind[lKey][search].length; i++){
                            results.push(ind[lKey][search][i]);
                        }
                    } else {               
                        for(var lMatch in ind[lKey]){
                            if(lMatch.indexOf(search) == 0){
                                for(var i = 0; i < ind[lKey][lMatch].length; i++){
                                    results.push(ind[lKey][lMatch][i]);
                                }
                                //results.concat(ind[lKey][lMatch]);
                            }
                        }
                    }
                }
            }
            return results;
        },
        
        // Parse name the same as we would in nameLists.searchNames
        parseName: function (name) {
            name = name.replace(/[^a-z0-9 ,]/ig, "");
            name = name.replace(/ +/ig, " ");
            name = name.replace(/,+/ig, ",")
            name = name.replace(/ ,/ig, ",");
            var result = {
                first:'',
                last:'',
                middle:'',
                exact_last:false,
                empty:true
            };
            var trimName = $.trim(name);
            if(!trimName.length){
                return result;
            }
            if(name.match(/ $/)){
                result.exact_last = true;
            }
            
            var searchParts = trimName.split(",");
            
            if(searchParts.length > 1){
                result.last = $.trim(searchParts[0]);
                var midParts = $.trim(searchParts[1]).split(" ");
                if(midParts.length > 1){
                    result.first = $.trim(midParts[0]);
                    result.middle = $.trim(midParts[1]);
                } else {
                    result.first = $.trim(searchParts[1]);
                }
                result.exact_last = true;
            } else {
                searchParts = trimName.split(" ");
                if(searchParts.length > 2){
                    result.last = $.trim(searchParts[2]);
                    result.middle = $.trim(searchParts[1]);
                    result.first = $.trim(searchParts[0]);
                } else if(searchParts.length > 1) {
                    result.last = $.trim(searchParts[1]);
                    result.first = $.trim(searchParts[0]);
                } else {
                    result.last = trimName;
                }
            }
            result.empty = (result.last.length<1);
            return result;
        },
        
        setOptions: function ( opt,o ) {
            
            if(!o){
                o = $(this);
            }
            
            var opts = o.data("ftMsOptions");
            if(!opts){
                opts = options;
            }
            if(typeof opt == "object"){
                //// console.log("setting options");
                o.data("ftMsOptions",$.extend({},opts,opt));
            } else {
                o.data("ftMsOptions",opts);
            }
            return o.data("ftMsOptions");
            
        },
        
        setDefaultOptions: function ( opt ) {
            
            if(typeof opt == "object"){
                //// console.log("setting options");
                $.extend(true, options,opt);
            }
            
        },
        
        getOptions: function (o) {
            
            if(!o){
                o = $(this);
            }
            
            var opts = o.data("ftMsOptions");
            if(!opts){
                opts = options;
            }
            return opts;
            
        },
       
        getDefaultOptions: function () {
            
            return options;
            
        }
        
    };
    
    $.fn.foreTeesMemberSelect = function ( method ){
        if (methods[method]) {
            return methods[ method ].apply (this, Array.prototype.slice.call( arguments, 1));
        } else if ( typeof method === 'object' || ! method) {
            return methods.init.apply( this, arguments );
        } else {
            $.error( 'Method ' + method + ' does not exist in jQuery.foreTeesMemberSelect' );
        }     
    }
    
})(jQuery);
