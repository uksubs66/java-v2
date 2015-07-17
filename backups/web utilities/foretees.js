var buttonSelected = false;

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
  childWindow.close();
  document.pgFrm.searchType.value = searchtype;
    document.pgFrm.nextAction.value = "addToList";
    document.pgFrm.submit();

}

function sendEmail(url, action)
{
    buttonSelected = true;
    document.pgFrm.action = url;
    document.pgFrm.nextAction.value = action;
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

  if (cancel == true)
  {
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

  if (del == true)
  {
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

  if (del == true)
  {
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

