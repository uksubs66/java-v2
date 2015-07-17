 /*
 *  Dynamic Search Box 
 *  Paul Sindelar
 *  ForeTees, LLC
 */

String.prototype.trim = function() {
    return this.replace(/^\s+|\s+$/g,"");
}
String.prototype.ltrim = function() {
    return this.replace(/^\s+/,"");
}
String.prototype.rtrim = function() {
    return this.replace(/\s+$/,"");
}

var timerId = null;
var timerId2 = null;
var skipChange = false;

function DYN_disableEnterKey(e) {
	var key;

	if(window.event)
		key = window.event.keyCode;     //IE
	else
		key = e.which;     				//firefox/safari

	if(key == 13)
		return false;
	else
		return true;
}

function DYN_moveOnEnterKey(e) {
	var key;

	if(window.event)
		key = window.event.keyCode;     //IE
	else
		key = e.which;     				//firefox/safari
            
        //console.log(key);

        switch (key){
            case 13: // Enter
                document.playerform.bname.onclick();
                break;
        }

}

function DYN_keyTrap(e) {
	var key;

	if(window.event)
		key = window.event.keyCode;     //IE
	else
		key = e.which;     				//firefox/safari
            
        //console.log(key);

        switch (key){
            case 38: // Up arrow
                DYN_doMove(-1);
                break;
            case 40: // Down arrow
                DYN_doMove(1);
                break;
        }

}

function DYN_triggerChange() {

    var sobj = document.forms["playerform"].DYN_search;
    //console.log('change');
    if(!sobj.DYN_boundArrows){
        sobj.DYN_boundArrows = true;
        sobj.onkeydown = DYN_keyTrap;
    }
    var origValue = sobj.value;
    origValue = origValue.ltrim();
    sobj.value = origValue;

    //DYN_doChange();
    if(!skipChange){
        if (timerId) {
            clearTimeout(timerId);
        }
        timerId = self.setTimeout(function(){DYN_doChange();}, 250);
    } else {
        skipChange = false;
    }

}

function DYN_doMove(change) {
    var f = document.forms["playerform"];
    var obj = f.bname;
    var max = obj.length;
    var boxSize = obj.size;
    if(!boxSize){
        boxSize = max;
    }
    boxSize --;
    var pos = obj.selectedIndex + change;
    if(pos < max && pos > -1){
        skipChange = true;
        obj.selectedIndex = pos;
        f.DYN_search.value = obj.options[pos].text;
        f.DYN_search.style.backgroundColor = "";
        //obj.selectedIndex = (pos+boxSize>(max-1))?max-1:pos;
        //obj.selectedIndex = (pos+boxSize>(max-1))?max-1:pos+boxSize;
        // IE9 wont allow a change of selectedIndex twice, so do it again
        // in a set timeout
        //setTimeout(function(){obj.selectedIndex = pos;f.DYN_search.value = obj.options[obj.selectedIndex].text;},1);
    }
    
}

function DYN_doChange() {

    var matchIndex = -1;
    var repregx = /[^ A-Z0-9_]/gi;
    var f = document.forms["playerform"];
    var origValue = f.DYN_search.value;
        origValue = origValue.replace(repregx,'').trim();
        //f.DYN_search.value = origValue;
    var stringSub = origValue.toUpperCase();
    var regx = new RegExp('^'+origValue.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&")+'.*','i');
    var max = f.bname.length;
    var low = 0;
    var high = max;
    var test_pos = (high >> 1);
    var last_test_pos = -1;
    var found = false;
    var lcount = 0;
    var obj = f.bname;
    
    var boxSize = obj.size;
    if(!boxSize){
        boxSize = max;
    }
    boxSize --;

    // if search box is empty then clean up and exit
    if (f.DYN_search.value == "") {
        f.bname.selectedIndex = -1;
        f.DYN_search.style.backgroundColor = "white";
        // ensure any running timer is canceled
        //if (timerId != null) {
        //    clearTimeout(timerId);
        //    timerId = null;
       // }
        return;
    }

  

    // Start optimized binary search of option list
    while (test_pos <= high){
        if(typeof(obj[test_pos].t_compare) != "string"){
            obj[test_pos].t_compare = obj[test_pos].text.toUpperCase().replace(repregx,'');
        }
        if(obj[test_pos].t_compare.match(regx)){
            matchIndex = test_pos;
            high = test_pos;
        } else if(obj[test_pos].t_compare >= stringSub){
            high = test_pos;
        }
        if(obj[test_pos].t_compare <= stringSub){
            low = test_pos;
        }
        var test_pos = ((high + low) >> 1);
        if(test_pos < 1){
            if(typeof(obj[test_pos].t_compare) != "string"){
                obj[test_pos].t_compare = obj[test_pos].text.toUpperCase().replace(repregx,'');
            }
            if(obj[test_pos].t_compare.match(regx)){
                matchIndex = test_pos;
            }
            break;
        }
        if(test_pos == last_test_pos){
            break;
        }
        last_test_pos = test_pos;
        lcount ++;
        if(lcount > max+1){
            break;
        }
    }
    
    found = (matchIndex > -1);
    
    if(!found && stringSub == 'TBA'){
        if(typeof(obj[0].t_compare) != "string"){
            obj[0].t_compare = obj[0].text.toUpperCase().replace(repregx,'');
            //console.log('SET:'+obj[test_pos].t_compare);
        }
        if(obj[0].t_compare.match(regx)){
            matchIndex = 0;
            found = true;
        }
    } else if(found){
        obj.DYN_lastSelectedIndex = matchIndex;
    } else if(!found && obj.DYN_lastSelectedIndex) {
        // If we didnt find anything, do a brute force search
        // near our last find, since somtimes first names are out of order.
        var last_found = obj.DYN_lastSelectedIndex;
        //console.log('SEARCHING FROM:'+last_found);
        low = last_found - 15;
        if(low < 0) low = 0;
        high = last_found + 15;
        if(high > max) high = max;
        for (var i=low; i< high; i++) {
            if(typeof(obj[i].t_compare) != "string"){
                obj[i].t_compare = obj[i].text.toUpperCase().replace(repregx,'');
                //console.log('SET:'+obj[test_pos].t_compare);
            }
            //var stringMain = obj[i].text.toUpperCase();
            //if (stringMain.indexOf(stringSub) == 0) {
            if(obj[i].t_compare.match(regx)){
                matchIndex = i;
                found = true;
                obj.DYN_lastSelectedIndex = matchIndex;
                break;
            }
        }
    }
    
    // End optimized binary search of option list
    
    /*
    // Alernate non-optimized search
    // loop thru to find match
    for (i=0; i< max; i++) {
        //var stringMain = obj[i].text.toUpperCase();
        //if (stringMain.indexOf(stringSub) == 0) {
        if(obj[i].text.match(regx)){
            matchIndex = i;
            found = true;
            break;
        }
    }
    */
    // if match found then select it and try to get
    // the selected item to the top of the select box
    /*
    if (found) {
        if (matchIndex + boxSize > f.bname.length) {
            f.bname.selectedIndex = matchIndex;
        } else {
            f.bname.selectedIndex = matchIndex + boxSize;
            f.bname.selectedIndex = matchIndex;
        }
        f.DYN_search.style.backgroundColor = "";
    } else {
        f.DYN_search.style.backgroundColor = "yellow";
    }
    */
   if(found){
        //obj.selectedIndex = matchIndex;
        obj.selectedIndex = (matchIndex+boxSize>(max-1))?max-1:matchIndex+boxSize;
        obj.selectedIndex = matchIndex;
        
        //obj.selectedIndex = (matchIndex+boxSize>(max-1))?max-1:matchIndex;
        // IE9 wont allow a change of selectedIndex twice, so do it again
        // in a set timeout
        
        if (timerId2) {
            clearTimeout(timerId2);
        }
        timerId2 = setTimeout(function(){obj.selectedIndex = matchIndex;},1);
        
        f.DYN_search.style.backgroundColor = "";
    } else {
        f.DYN_search.style.backgroundColor = "yellow";
    }
   /*
    // ensure timer is canceled
    if (timerId != null) {
        clearTimeout(timerId);
        timerId = null;
    }
*/
    // if contents of search box changed while running, re run
    //if (f.DYN_search.value != origValue) DYN_doChange();

}

function DYN_selectClick(obj, func){
    if(!DYN_isIos() && !DYN_isAndroid()){
        func(obj.value);
    }
}

function DYN_selectChange(obj, func){
    if(DYN_isAndroid()){
        func(obj.value);
    }
}

function DYN_selectBlur(obj, func){
    if(DYN_isIos()){
        func(obj.value);
    }
}

function DYN_isAndroid(){
    return navigator.userAgent.match(/(Android|BlackBerry)/);
}

function DYN_isIos(){
    return navigator.userAgent.match(/(iPhone|iPad|iPod)/);
}