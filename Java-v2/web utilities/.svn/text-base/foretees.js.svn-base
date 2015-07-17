var buttonSelected = false;
var isDisabled = false; // when set to true sheet will cease to function
var rv = "";

function preloadImage(imageName) 
{
    var image = new Image();
    image.src = imageName;
}
  
function openNewWindow(url, winName, opts)
{
    window.open(url, winName, opts);
}

function execute(url, action, searchType)
{
    buttonSelected = true;
    document.pgFrm.nextAction.value = action;
    document.pgFrm.searchType.value = searchType;
    document.pgFrm.submit();
}

function returnSelectedItems(prefix, searchtype)
{
  //get the selected checkboxes or inputs and send them to the parent
  var selected = document.getElementsByTagName("input");
  var selectedStr = "";
  if(selected)
  {
    for (var i=0; i<selected.length;i++)
    {
        if ((selected[i].name).indexOf("cb_" + prefix) > -1)
        {
          selectedStr = selectedStr + selected[i].value + ";";
        }
    }
  }
  opener.addSelectedItems(selectedStr, window, searchtype);
}

function returnCheckedItems(prefix, searchtype)
{
  //get the selected checkboxes or inputs and send them to the parent
  var selected = document.getElementsByTagName("input");
  var selectedStr = "";
  if(selected)
  {
    for (var i=0; i<selected.length;i++)
    {
        if ((selected[i].name).indexOf("cb_" + prefix) > -1)
        {
          if (selected[i].checked)
          {
            selectedStr = selectedStr + selected[i].value + ";";
          }
        }
    }
  }
  opener.addSelectedItems(selectedStr, window, searchtype);
}

function addSelectedItems(items, childWindow, searchtype)
{
    buttonSelected = true;
    document.pgFrm.selItemsStr.value = items;
    //document.pgFrm.recipients.value = items;
    childWindow.close();
    document.pgFrm.searchType.value = searchtype;
    document.pgFrm.nextAction.value = "addToList";
    document.pgFrm.submit();

}

function sendEmail(url, action)
{
    if (document.pgFrm.formId != null) {        // if caller is the Send Email form (not from the tee sheet CP)
		var content = "";
		try {
			content = tinyMCE.get('messageField').getContent();
		} catch (exception) {
			content = document.pgFrm.messageField.value;
		}
    	if (document.pgFrm.recipients == null || (document.pgFrm.recipients.value == "" && document.pgFrm.recipients.length == undefined)) {
    		alert("You haven't selected any recipients.  Please add at least one member to the list.");
    		return;
    	} else if (document.pgFrm.subjectField.value == "") {
    		alert("You must specify a subject for your email message.  Please correct and try again.");
    		return;
    	} else if (content == "") {
    		alert("You must specify a message for your email.  Please correct and try again.");
    		return;
    	}
    	document.pgFrm.enctype = "multipart/form-data"; 	// DOM
    	document.pgFrm.encoding = "multipart/form-data";  	// IE
    }
    buttonSelected = true;
    document.pgFrm.action = url;
    document.pgFrm.nextAction.value = action;
    if(document.getElementById("sendLink")) {
      document.getElementById("sendLink").innerHTML='Wait';  			// change word on button
      document.getElementById("sendLink").href='javascript:void(0)';  	// disable the button
    }
    document.pgFrm.submit();
}

function sendPush(url, action)
{
    if (document.pgFrm.formId != null) {        // if caller is the Send Email form (not from the tee sheet CP)
        var content = document.pgFrm.messageField.value;
    	if (document.pgFrm.recipients == null || (document.pgFrm.recipients.value == "" && document.pgFrm.recipients.length == undefined)) {
            alert("You haven't selected any recipients.  Please add at least one member to the list.");
            return;
    	} else if (content == "") {
            alert("You must specify a message for your push notification.  Please correct and try again.");
            return;
    	}
    } else {
        var input = document.createElement("input");
        input.type = "hidden";
        input.name = "push";
        input.value = "1";
        document.pgFrm.appendChild(input);
    }
    buttonSelected = true;
    document.pgFrm.action = url;
    //document.pgFrm.nextAction.value = "sendPush";
    document.pgFrm.nextAction.value = action;
    if(document.getElementById("sendLink")) {
        document.getElementById("sendLink").innerHTML='Wait';  			// change word on button
        document.getElementById("sendLink").href='javascript:void(0)';  	// disable the button
    }
    document.pgFrm.submit();
}

function goTo(url)
{

    window.location = url;
}

function view(url, objName, courseName)
{
    document.pgFrm.action = url;
    document.pgFrm.name.value = objName;
    document.pgFrm.course.value = courseName;
    document.pgFrm.submit();
}

function viewUser(url, objName)
{
    document.pgFrm.action = url;
    document.pgFrm.username.value = objName;
    document.pgFrm.submit();
}

function viewProshopUser(url, objName, activity_id)
{
	
	document.forms["pgFrm"].action = url;
	document.forms["pgFrm"].username.value = objName;
	
	document.forms["pgFrm"].activity_id.value = activity_id;
	
	document.forms["pgFrm"].submit();

}

function reloadProshopUser(url, objName, activity_id)
{
	
	document.forms["pgFrm"].action = url;
	
	document.forms["pgFrm"].username.value = objName;
	
	document.forms["pgFrm"].activity_id.value = activity_id;
	
	document.forms["pgFrm"].reload.value = "true";
	
	document.forms["pgFrm"].submit();

}

function viewList(url, objName, action)
{
    document.pgFrm.action = url;
    document.pgFrm.listName.value = objName;
    document.pgFrm.nextAction.value = action;

    document.pgFrm.submit();
}

function addList(url, action)
{
    document.pgFrm.action = url;
    document.pgFrm.currentAction.value = action;

    document.pgFrm.submit();
}

function constructEmail(url, action)
{
    document.pgFrm.action = url;
    document.pgFrm.currentAction.value = action;
    document.pgFrm.submit();
}

function update(url, action)
{
    buttonSelected = true;
    document.pgFrm.action = url;  
    document.pgFrm.nextAction.value = action;
    document.pgFrm.submit();
}

function updateProshopUser(url, action, activity_id)
{
    buttonSelected = true;
    document.forms["pgFrm"].action = url;  
    document.forms["pgFrm"].nextAction.value = action;
    document.forms["pgFrm"].activity_id.value = activity_id;
    document.forms["pgFrm"].submit();
}

function addNameToList(url, nextAction)
{
    buttonSelected = true;
    document.pgFrm.action = url;
    document.pgFrm.nextAction.value = nextAction;
    document.pgFrm.submit();
}

function removeNameFromList(url, nextAction, username)
{
    buttonSelected = true;
    document.pgFrm.action = url;  
    document.pgFrm.nextAction.value = nextAction;
    document.pgFrm.username.value = username;
    document.pgFrm.submit();
}

function search(url, action, nextAction, theLetter)
{
    buttonSelected = true;
    document.pgFrm.letter.value = theLetter;
    document.pgFrm.action = url;  
    document.pgFrm.action.value = action;
    document.pgFrm.nextAction.value = nextAction;
    document.pgFrm.submit();
}

function cancel(url, action, message)
{
    var cancel = confirm(message);
    if (cancel == true) {
        buttonSelected = true;
        document.pgFrm.action = url;  
        document.pgFrm.nextAction.value = action;
        document.pgFrm.submit();
    }
}

function deleteUser(url, username, displayName)
{
    var message = "Are you sure you want to delete " + displayName + " from the database?";
    var del = confirm(message);

    if (del == true) {
       document.pgFrm.action = url; 
       document.pgFrm.username.value = username;
       document.pgFrm.nextAction.value = "delete"; 
       document.pgFrm.submit();
    } 
}

function deleteList(url, listname, action)
{
    var message = "Are you sure you want to delete the list \"" + listname + "\" from the database?";
    var del = confirm(message);

    if (del == true) {
       document.pgFrm.action = url; 
       document.pgFrm.listName.value = listname;
       document.pgFrm.nextAction.value = "delete"; 
       document.pgFrm.submit();
    }
}

function cursor()
{
  document.f.memname_first.focus();
}

function displayFeedbackMessage(message)
{
  if (message != "")
  {
    alert(message);
  }
}

function setPasswordToLastName()
{
    document.pgFrm.mem_password.value = document.pgFrm.memname_last.value.substring(0,10);
}

function cleanup(url, action, message)
{
    if(!buttonSelected)
    {
        alert(message);
        document.pgFrm.action = url;  
        document.pgFrm.nextAction.value = action;
        document.pgFrm.submit();
    }
  
}

function exeMenuAction(url, method)
{
  if (method == "get") {
     exeMenuAsUrl(url);
  } else {
     var rev = document.forms.mnuHlp.revLevel.value;
     document.forms.mnuHlp.action = "/" + rev + "/" + url;
     document.forms.mnuHlp.target = "bot";
     document.forms.mnuHlp.method = method;
     document.mnuHlp.submit();
  }
}

function exeMenuAsUrl(url)
{
    var rev = document.forms.mnuHlp.revLevel.value;
    url = "/" + rev + "/" + url;
    parent.bot.location.href = url;
}

function exeControlPanelAction(print, fsz, excel, order, double_line)
{
    document.forms.cpHlp.target = "_blank";
    document.forms.cpHlp.method = "post";
    document.forms.cpHlp.print.value = print;
    document.forms.cpHlp.fsz.value = fsz;
    document.forms.cpHlp.excel.value = excel;
    document.forms.cpHlp.order.value = order;
    document.forms.cpHlp.double_line.value = double_line;
    document.cpHlp.submit();
}

function checkForAttachments() 
{
	
    if (document.pgFrm.attach1.value != '' || document.pgFrm.attach2.value != '' || document.pgFrm.attach3.value != '') {
        alert('Your attachments have been cleared and you will need to reselect them if you wish to include them.  To avoid this, finalize your recipients before attaching your files.  This is a new security feature of your web browser.');
    }
    
}


// show the transportation options popup window
function showTOPopupLite(el) {
    if (isDisabled == true) {return} // cancelTOPopup();
    if (el.getAttribute("value") == "") {return} // if the trans opt span value property is empty then it is not editable
    setDisabled(true);
    p = document.getElementById("elTOPopup");

    // make sure there is enough room for the popup and decide how to position it
    var lastMousePosX = 0;
    var lastMousePosY = 0;
    var elem = el;

    if (elem.offsetParent) {
        do {
            lastMousePosX += elem.offsetLeft
            lastMousePosY += elem.offsetTop
        } while (elem = elem.offsetParent)
    }

    lastMousePosX += 10;
    lastMousePosY += 15;

    // check the y axis
    if ((lastMousePosY - document.body.scrollTop + p.offsetHeight) > document.body.offsetHeight) {
            p.style.top = (lastMousePosY - p.offsetHeight) + "px";
    } else {
            p.style.top = lastMousePosY + "px";
    }
    // check the x axis
    if (lastMousePosX + 120 > 960) {
            p.style.left = (lastMousePosX - 120) + "px";
    } else {
            p.style.left = lastMousePosX + "px";
    }
    p.style.zIndex = 1000; // set the z-order index to top
    p.style.visibility = "visible"; // show it
    p.style.display = "block"; // show it

    p.setAttribute("defaultValue", el.getAttribute("value"));

    attr = el.attributes;
    tmode = attr.getNamedItem("value").value;

    var ps = attr.getNamedItem("playerSlot").value;
    var tid = attr.getNamedItem("tid").value;
    var p9 = attr.getNamedItem("p9").value;

    p.setAttribute("playerSlot", ps);
    p.setAttribute("tid", tid);

    f = document.forms["frmTransOpt"];
    for (x=0;x<=g_transOptTotal-1; x++) {
        if (f.to[x].value == tmode.substring(0, 3)) { rv = f.to[x].checked = true; }
    }

    // set default for "change all" form element
    f.changeAll.checked = false;

    // set default for "9 hole" form element
    f.nh.checked = (p9 == "1");
    p.setAttribute("nh", ((p9 == "1") ? "true" : "false"));
    //f.nh.checked = (tmode.substring(tmode.length - 1, tmode.length) == "9");
    //p.setAttribute("nh", ((tmode.substring(tmode.length - 1, tmode.length) == "9") ? "true" : "false"));
}

// close and process the selections for the
// transportation options popup window
function saveTOPopupLite() {
    f = document.forms["frmTransOpt"];
    for (x=0;x<=g_transOptTotal-1; x++) {
            if (f.to[x].checked == true) { rv = f.to[x].value }
    }
    tmpA = (f.changeAll.checked == true) ? "true" : "false";
    tmpB = (f.nh.checked == true) ? "true" : "false";

    p = document.getElementById("elTOPopup");
    var attr = p.attributes;

    if (rv != undefined) {
        if ((rv.substring(0, 3) != attr.getNamedItem("defaultValue").value.substring(0, 3)) || (attr.getNamedItem("nh").value != tmpB)) {

            newcw = "" + rv;
            if (tmpB == "true") newcw += "9";

            //alert('p.nh=' + p.getAttribute("nh") + "\n" + "tmode=" + rv + "\n" + "playerSlot=" + p.getAttribute("playerSlot") + "\n" + "tid=" + p.getAttribute("tid") + "\nchangeAll=" + tmpA + "\nninehole=" + tmpB);

            $.get("/v5/servlet/Proshop_sheet_utilities", { todo: "updCW", tmode: rv, tid: p.getAttribute("tid"), playerSlot: p.getAttribute("playerSlot"), changeAll: tmpA, ninehole: tmpB},
            function(data) {
                //alert("Data Returned: " + data);
                if (data.substring(0, 2) == "OK") {
                    if (tmpA == "true") {
                        try {
                            cwbox = document.getElementById("cw_" + p.getAttribute("tid") + "_1");
                            cwbox.innerHTML = newcw;
                            cwbox.setAttribute("value", newcw); // change the default so when the box is redisplayed it pre-selects the correct tmode
                        } catch (err) {}
                        try {
                            cwbox = document.getElementById("cw_" + p.getAttribute("tid") + "_2");
                            cwbox.innerHTML = newcw;
                            cwbox.setAttribute("value", newcw);
                        } catch (err) {}
                        try {
                            cwbox = document.getElementById("cw_" + p.getAttribute("tid") + "_3");
                            cwbox.innerHTML = newcw;
                            cwbox.setAttribute("value", newcw);
                        } catch (err) {}
                        try {
                            cwbox = document.getElementById("cw_" + p.getAttribute("tid") + "_4");
                            cwbox.innerHTML = newcw;
                            cwbox.setAttribute("value", newcw);
                        } catch (err) {}
                        try {
                            cwbox = document.getElementById("cw_" + p.getAttribute("tid") + "_5");
                            cwbox.innerHTML = newcw;
                            cwbox.setAttribute("value", newcw);
                        } catch (err) {}

                    } else {
                        cwbox = document.getElementById("cw_" + p.getAttribute("tid") + "_" + p.getAttribute("playerSlot") + '');
                        cwbox.innerHTML = newcw;
                        cwbox.setAttribute("value", newcw);
                    }
                } else {
                    alert('Failed to update tmode!');
                }
            });
        }
    }
    p.style.visibility = "hidden";
    p.style.display = "none";
    p.style.left = "-500px";
    setDisabled(false);
}

// close and discard any changes to the fbo popup window
function cancelTOPopup() {
    document.getElementById("elTOPopup").style.visibility = "hidden";
    document.getElementById("elTOPopup").style.display = "none";
    document.getElementById("elTOPopup").style.left = "-500px";
    setDisabled(false);
}

// show the Pay Now options popup window
function showPNPopupLite(el) {

    //if (isDisabled == true) {return}
    //if (el.getAttribute("value") == "") {console.log("value blank, returning"); return} // if the pay now span value property is empty then it is not editable
    if (el.getAttribute("value") == "") {return} // if the pay now span value property is empty then it is not editable
    setDisabled(true);
    p = document.getElementById("elPNPopup");

    // make sure there is enough room for the popup and decide how to position it
    var lastMousePosX = 0;
    var lastMousePosY = 0;
    var elem = el;

    if (elem.offsetParent) {
        do {
            lastMousePosX += elem.offsetLeft
            lastMousePosY += elem.offsetTop
        } while (elem = elem.offsetParent)
    }

    lastMousePosX += 10;
    lastMousePosY += 15;
    
    // check the y axis
    if ((lastMousePosY - document.body.scrollTop + p.offsetHeight) > document.body.offsetHeight) {
            p.style.top = (lastMousePosY - p.offsetHeight) + "px";
    } else {
            p.style.top = lastMousePosY + "px";
    }

    // check the x axis
    if (lastMousePosX + 120 > 960) {
            p.style.left = (lastMousePosX - 120) + "px";
    } else {
            p.style.left = lastMousePosX + "px";
    }
    p.style.zIndex = 1000; // set the z-order index to top
    p.style.visibility = "visible"; // show it

    p.setAttribute("defaultValue", el.getAttribute("value"));

    attr = el.attributes;
    payopt = attr.getNamedItem("value").value;

    var ps = attr.getNamedItem("playerSlot").value;
    var tid = attr.getNamedItem("tid").value;

    p.setAttribute("playerSlot", ps);
    p.setAttribute("tid", tid);

    f = document.forms["frmPayNowOpt"];

    if (f.pn[0].value == payopt) { rv = f.pn[0].checked = true; }
    else if (f.pn[1].value == payopt) { rv = f.pn[1].checked = true; } 
    else if (f.pn[2].value == payopt) { rv = f.pn[2].checked = true; } 
    else if (f.pn[3].value == payopt) { rv = f.pn[3].checked = true; } 

    // set default for "change all" form element
    f.changeAll.checked = false;
}

// close and process the selections for the
// Pay Now options popup window
function savePNPopupLite() {
    
    f = document.forms["frmPayNowOpt"];
    for (x=0; x<5; x++) {
        if (f.pn[x].checked == true) { rv = f.pn[x].value }
    }
    tmpA = (f.changeAll.checked == true) ? "true" : "false";
    
    p = document.getElementById("elPNPopup");
    var attr = p.attributes;
    
    if (rv != undefined) {
        
        if (rv != attr.getNamedItem("defaultValue")) {

            newpn = "" + rv;

            pnimg = "<img id=\"paybox_"+ p.getAttribute("tid") + "_" + p.getAttribute("playerSlot") + "\" src=\"/v5/images/" + (newpn == 0 ? "pospaynow.gif" : "pospaid.gif") 
                    + "\" border=\"1\" name=\"paynow\" title=\"" + (newpn == 0 ? "Click here to select a Payment Method for this player." : "Click here to adjust or remove the selected Payment Method for this player.") + "\">";

            $.get("/v5/servlet/Proshop_sheet_utilities", { todo: "updPN", pos: rv, tid: p.getAttribute("tid"), playerSlot: p.getAttribute("playerSlot"), changeAll: tmpA},
               function(data) {
                 if (data.substring(0, 2) == "OK") {

                     if (tmpA == "true") {
                         try {
                             pnbox = document.getElementById("pn_" + p.getAttribute("tid") + "_1");
                             pnbox.innerHTML = pnimg;
                             pnbox.setAttribute("value", newpn); // change the default so when the box is redisplayed it pre-selects the correct tmode
                         } catch (err) {}
                         try {
                             pnbox = document.getElementById("pn_" + p.getAttribute("tid") + "_2");
                             pnbox.innerHTML = pnimg;
                             pnbox.setAttribute("value", newpn);
                         } catch (err) {}
                         try {
                             pnbox = document.getElementById("pn_" + p.getAttribute("tid") + "_3");
                             pnbox.innerHTML = pnimg;
                             pnbox.setAttribute("value", newpn);
                         } catch (err) {}
                         try {
                             pnbox = document.getElementById("pn_" + p.getAttribute("tid") + "_4");
                             pnbox.innerHTML = pnimg;
                             pnbox.setAttribute("value", newpn);
                         } catch (err) {}
                         try {
                             pnbox = document.getElementById("pn_" + p.getAttribute("tid") + "_5");
                             pnbox.innerHTML = pnimg;
                             pnbox.setAttribute("value", newpn);
                         } catch (err) {}

                     } else {
                         pnbox = document.getElementById("pn_" + p.getAttribute("tid") + "_" + p.getAttribute("playerSlot") + '');
                         pnbox.innerHTML = pnimg;
                         pnbox.setAttribute("value", newpn);
                     }
                 } else {
                     alert('Failed to update Pay Now option!');
                 }
             });
         }
     }
     p.style.visibility = "hidden";
     p.style.left = "-500px";
     setDisabled(false);
}

// close and discard any changes to the fbo popup window
function cancelPNPopup() {
    document.getElementById("elPNPopup").style.visibility = "hidden";
    document.getElementById("elPNPopup").style.left = "-500px";
    setDisabled(false);
}

// show the guest information popup window
function showGUESTPopup(el) {

    if (isDisabled == true) {return} // cancelGUESTPopup();
    if (el.getAttribute("value") == "") {return} // if the trans opt span value property is empty then it is not editable
    //setDisabled(true);
    p = document.getElementById("elGUESTPopup");

    // make sure there is enough room for the popup and decide how to position it
    var lastMousePosX = 0;
    var lastMousePosY = 0;
    var elem = el.options[el.selectedIndex];

    if (elem.offsetParent) {
        do {
            lastMousePosX += elem.offsetLeft
            lastMousePosY += elem.offsetTop
        } while (elem = elem.offsetParent)
    }

    lastMousePosX += 10;
    lastMousePosY += 15;
    
    // check the y axis
    if ((lastMousePosY - document.body.scrollTop + p.offsetHeight) > document.body.offsetHeight) {
            p.style.top = (lastMousePosY - p.offsetHeight) + "px";
    } else {
            p.style.top = lastMousePosY + "px";
    }
    // check the x axis
    if (lastMousePosX + 120 > 960) {
            p.style.left = (lastMousePosX - 120) + "px";
    } else {
            p.style.left = lastMousePosX + "px";
    }

    //p.style.left = 0;
    //p.style.top = 0;

    p.style.zIndex = 1000; // set the z-order index to top
    p.style.visibility = "visible"; // show it
    //alert('hi:'+lastMousePosX+','+lastMousePosY);
}

// close and discard any changes to the fbo popup window
function cancelGUESTPopup() {
    document.getElementById("elGUESTPopup").style.visibility = "hidden";
    document.getElementById("elGUESTPopup").style.left = "-500px";
    setDisabled(false);
}

// set the isDisabled variable and change style sheet accordindly
// this is called when enabling or disabling the tee sheet from user interaction
function setDisabled(pValue) {
    isDisabled = pValue;
}

function memTOname(el, num) {
    var skip = 0;
    var doLookup = false;
    var memNum = el.value;
    memNum = memNum.trim(); // what else b4 checking

    // only perform the lookup if the contents of the textbox match the memNum format
    //if ( memNum != "" && !isNaN(memNum) ) {
    if ( memNum != "" ) {        // changed for Desert Mtn so we can enter alphanumeric usernames (1234A) BP 12/21/12
        doLookup = true;
    } else {
        // perform some other checks - strip first 1 or 2, last 1 or 2 and check again
    }

    if ( doLookup ) {

        $.get("/v5/servlet/data_loader", { memNumTOname: memNum },  
         function(data){
            //alert("Data Loaded: " + data);  
            array = data.split(':');
            var name = array[0];
            var wc = array[1];
            var f = document.forms['playerform'];
            var player1 = f.player1.value;
            var player2 = f.player2.value;
            var player3 = f.player3.value;
            var player4 = f.player4.value;
            var player5 = f.player5.value;

            if (( name == player1) || ( name == player2) || ( name == player3) || ( name == player4) || ( name == player5)) {
                skip = 1;
            }

            if (skip == 0) {
                nameElem = "player"+num;
                tmodeElem = "p"+num+"cw";
                guestElem = "guest_id"+num;
                document.playerform[nameElem].value = name;
                if ((wc != null) && (wc != '')) {
                    document.playerform[tmodeElem].value = wc;
                }
                document.playerform[guestElem].value = "0";
                //document.playerform[box].value = name;
                //f.guest_id1.value = '0';
            }
        });
    }
}
 