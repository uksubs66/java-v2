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
                skipChange = true;
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
            case 39: // Forward arrow
                skipChange = true;
                var obj = document.forms["playerform"].bname;
                
                if(!obj.DYN_JumpHistory.length || !(obj.DYN_JumpHistory[obj.DYN_JumpHistory.length-1] == obj.selectedIndex)){
                    obj.DYN_JumpHistory.push(obj.selectedIndex);
                }
                DYN_doChange(obj.selectedIndex + 1,true);
                break;
            case 37: // Back arrow
                skipChange = true;
                var obj = document.forms["playerform"].bname;
                if(obj.DYN_JumpHistory && obj.DYN_JumpHistory.length){
                    var back = obj.DYN_JumpHistory.pop();
                    //console.log("jump back:"+back);
                    DYN_doChange(back,true);
                }
                return false;
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
        var lobj = document.forms["playerform"].bname;
        lobj.DYN_JumpHistory = [];
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
    }
    
}

function DYN_doChange(start, no_top) {

    var matchIndex = -1;
    //var repregx = /^(&nbsp;)+|^ +| +|[,]/gi;
    var f = document.forms["playerform"];
    var origValue = f.DYN_search.value;
    //    origValue = origValue.replace(repregx,'').trim();
        //f.DYN_search.value = origValue;
    var stringSub = origValue.toUpperCase().trim();
    var regx;
    if(parseInt(stringSub,10)+'' == stringSub){
        // Search for member number at end of string
        regx = new RegExp('\\s'+origValue.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&")+'$','i');
    } else {
        // Search for last name at begining of string
        regx = new RegExp('^\\s*'+origValue.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&")+'','i');
    }
    var max = f.bname.length;
    
    if(!start){
        start = 0;
    } 
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
        f.DYN_search.style.backgroundColor = "";

        return;
    }
    /*
    // Start optimized binary search of option list
    var low = start;
    var high = max;
    var test_pos = 0;
    var last_test_pos = -1;
    while (test_pos <= high){
        var test_pos = ((high + low) >> 1);  
        if(typeof(obj[test_pos].t_compare) != "string"){
            obj[test_pos].t_compare = obj[test_pos].text.toUpperCase().replace(repregx,'');
        }
        if(obj[test_pos].t_compare.match(regx)){
            matchIndex = test_pos;
        }
        if(test_pos < 1 || test_pos == last_test_pos || lcount > max+1){
            break;
        }
        last_test_pos = test_pos;
        lcount ++;
        if(obj[test_pos].t_compare >= stringSub){
            high = test_pos;
        }
        if(obj[test_pos].t_compare <= stringSub){
            low = test_pos;
        }
    }
    
    found = (matchIndex > -1);
    
    if(!found){
        for (var i=0; i < max; i++) {
            if(typeof(obj[i].t_compare) != "string"){
                obj[i].t_compare = obj[i].text.toUpperCase().replace(repregx,'');
            }
            if(obj[i].t_compare.match(regx)){
                matchIndex = i;
                found = true;
                obj.DYN_lastSelectedIndex = matchIndex;
                break;
            }
        }
    }
    */
   for (var i=start; i < max; i++) {
        //if(obj[i].text.toUpperCase().trim().indexOf(stringSub) == 0){
        if(obj[i].text.match(regx)){
            matchIndex = i;
            found = true;
            break;
        }
   }
    if(found){
        if(no_top){
            obj.selectedIndex = matchIndex;
        }else{
            obj.selectedIndex = (matchIndex+boxSize>(max-1))?max-1:matchIndex+boxSize;
            obj.selectedIndex = matchIndex;
        
            // IE9 somtimes wont scroll to the selectedIndex if set twice, so do it again
            // in a set timeout
            if (timerId2) {
                clearTimeout(timerId2);
            }
            timerId2 = setTimeout(function(){
                obj.selectedIndex = matchIndex;
            },1);
        }
        f.DYN_search.style.backgroundColor = "";
    } else {
        f.DYN_search.style.backgroundColor = "yellow";
    }

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