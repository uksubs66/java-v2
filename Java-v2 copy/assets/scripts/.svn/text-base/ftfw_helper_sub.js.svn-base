/*

ForeTees / Flexscape Integration Helper Sub File

(included as an external script in the comm helper iframe)

*/

// Tell the parent iframe what height the iframe needs to be
function parentIframeResize() {
    var data = getParam('pm');
    if(!data.length){
        // Old
        var height = getParam('height');
        // This works as our parent's parent is on our domain..
        parent.parent.resizeIframe(height);
    } else {
        // IE7 workaround for lacking postMessage
        if(parent && parent.parent.ftMessageHandler){
            parent.parent.ftMessageHandler.call(parent.parent, {source:parent, data:decodeURIComponent(data), origin:window.location.url});
        };
    }
}

function closeIframe() {
	parent.parent.closeIframe();
}

// Helper function, parse param from request string
function getParam( name ) {
	name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
	var regexS = "[\\?&]"+name+"=([^&#]*)";
	var regex = new RegExp( regexS );
	var results = regex.exec( window.location.href );
	if( results == null )
		return null;
	else
		return results[1];
}