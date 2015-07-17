//----------DHTML Menu Created using AllWebMenus PRO ver 3.1-#532---------------
//C:\JAVA\Tomcat\webapps\v5\web utilities\proshop\ForeteesControlPanel.awm
var awmMenuName='foreteesControlPanel';
var awmLibraryBuild=532;
var awmLibraryPath='/awmData-foreteesControlPanel';
var awmImagesPath='/awmData-foreteesControlPanel';
var awmSupported=(navigator.appName + navigator.appVersion.substring(0,1)=="Netscape5" || document.all || document.layers || navigator.userAgent.indexOf('Opera')>-1)?1:0;
if (awmAltUrl!='' && !awmSupported) window.location.replace(awmAltUrl);
if (awmSupported){
var awmMenuPath;
if (document.layers) mpi=((document.images['awmMenuPathImg-foreteesControlPanel'])?document.images['awmMenuPathImg-foreteesControlPanel'].src:document.layers['xawmMenuPathImg-foreteesControlPanel'].document.images['awmMenuPathImg-foreteesControlPanel'].src); else mpi=document.images['awmMenuPathImg-foreteesControlPanel'].src;
awmMenuPath=mpi.substring(0,mpi.length-16);
while (awmMenuPath.search("'")>-1) {awmMenuPath=awmMenuPath.replace("'", "&#39;");}
var nua=navigator.userAgent,scriptNo=(nua.indexOf('Safari')>-1)?7:(nua.indexOf('Gecko')>-1)?2:((document.layers)?3:((nua.indexOf('Opera')>-1)?4:((nua.indexOf('Mac')>-1)?5:((nua.indexOf('Konqueror')>-1)?6:1))));
document.write("<SCRIPT SRC='"+awmMenuPath+awmLibraryPath+"/awmlib"+scriptNo+".js'><\/SCRIPT>");
var n=null;
awmzindex=1000;
}

var awmSubmenusFrame='';
var awmSubmenusFrameOffset;
var awmOptimize=0;
function awmBuildMenu(){
if (awmSupported){
awmImagesColl=['rightArrow.gif',10,9,'green_haschild.gif',10,10];
awmCreateCSS(1,2,1,'#FFFFFF','#3A414E',n,'bold 12px sans-serif',n,'solid',1,'#3A414E',0,4);
awmCreateCSS(0,1,0,n,n,n,n,n,'solid',1,n,0,0);
awmCreateCSS(1,2,1,'#FFFFFF','#8B8970',n,'12px Verdana','underline','solid',1,'#8B8970',0,1);
awmCreateCSS(0,2,1,'#CCCCAA','#8B8970',n,'12px Verdana','underline','solid',1,'#8B8970',0,1);
awmCreateCSS(0,2,1,'#CCCCAA','#8B8970',n,'12px Verdana','underline','inset',2,'#8B8970',0,1);
awmCreateCSS(1,2,1,'#FFFFFF','#3A414E',n,'bold 12px Verdana',n,'solid',1,'#3A414E',0,1);
awmCreateCSS(0,1,0,n,'#CCCCAA',n,n,n,'solid',1,n,0,0);
awmCreateCSS(1,2,0,'#336633','#CCCCAA',n,'bold 12px sans-serif',n,'none',1,'#CCCCAA',0,1);
awmCreateCSS(0,2,0,'#000000','#F5F5DC',n,'bold 12px sans-serif',n,'none',1,'#F5F5DC',0,1);
awmCreateCSS(0,2,0,'#800040','#F5F5DC',n,'bold 12px sans-serif',n,'none',1,'#F5F5DC',0,1);
awmCreateCSS(1,2,1,'#FFFFFF','#3A414E',n,'bold 12px sans-serif',n,'solid',1,'#3A414E',0,0);
var s0=awmCreateMenu(0,0,0,0,1,0,0,0,0,30,30,0,0,1,0,n,"",n,1,0,1,0,n,n,100,0);
it=s0.addItemWithImages(2,3,4,"&nbsp;&nbsp;Print Options&nbsp;&nbsp;",n,n,"Select the report type to print",0,n,n,1,1,1,n,n,n,"",n,n,n,n,n);
var s1=it.addSubmenu(0,0,-4,0,0,0,0,6,5,n,"Select the type of report you would like to print",n,1,0,1,0,"awmhidediv();","awmshowdiv();",100);
it=s1.addItemWithImages(7,8,9,"&nbsp;&nbsp;Tee Sheet&nbsp;&nbsp;",n,n,"",n,n,n,0,0,0,1,1,1,"",n,n,n,n,n);
var s2=it.addSubmenu(0,0,-4,0,0,0,0,6,10,n,"",n,1,0,1,0,n,n,100);
it=s2.addItemWithImages(7,8,9,"&nbsp;&nbsp;Single Line&nbsp;&nbsp;",n,n,"",n,n,n,0,0,0,1,1,1,"",n,n,n,n,n);
var s3=it.addSubmenu(0,0,-4,0,0,0,0,6,5,n,"",n,1,0,1,0,n,n,100);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Small Font&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('print', '1', 'no', '', '0')",n,n);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Large Font&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('print', '2', 'no', '', '0')",n,n);
it=s2.addItemWithImages(7,8,9,"&nbsp;&nbsp;Double Line&nbsp;&nbsp;",n,n,"",n,n,n,0,0,0,1,1,1,"",n,n,n,n,n);
var s3=it.addSubmenu(0,0,-4,0,0,0,0,6,5,n,"",n,1,0,1,0,n,n,100);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Small Font&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('print', '1', 'no', '', '1')",n,n);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Large Font&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('print', '2', 'no', '', '1')",n,n);
it=s2.addItem(7,8,9,"&nbsp;&nbsp;Excel Format&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('print', '2', 'yes', '', '0')",n,n);
it=s1.addItemWithImages(7,8,9,"&nbsp;&nbsp;Bag Report&nbsp;&nbsp;",n,n,"",n,n,n,0,0,0,1,1,1,"",n,n,n,n,n);
var s2=it.addSubmenu(0,0,-4,0,0,0,0,1,10,n,"",n,1,0,1,0,n,n,100);
it=s2.addItemWithImages(7,8,9,"&nbsp;&nbsp;Single Line&nbsp;&nbsp;",n,n,"",n,n,n,0,0,0,1,1,1,"",n,n,n,n,n);
var s3=it.addSubmenu(0,0,-4,0,0,0,0,6,5,n,"",n,1,0,1,0,n,n,100);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Small Font&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('report', '1', 'no', '', '0')",n,n);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Large Font&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('report', '2', 'no', '', '0')",n,n);
it=s2.addItemWithImages(7,8,9,"&nbsp;&nbsp;Double Line&nbsp;&nbsp;",n,n,"",n,n,n,0,0,0,1,1,1,"",n,n,n,n,n);
var s3=it.addSubmenu(0,0,-4,0,0,0,0,6,5,n,"",n,1,0,1,0,n,n,100);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Small Font&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('report', '1', 'no', '', '1')",n,n);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Large Font&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('report', '2', 'no', '', '1')",n,n);
it=s2.addItem(7,8,9,"&nbsp;&nbsp;Excel Format&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('report', '2', 'yes', '', '0')",n,n);
it=s1.addItemWithImages(7,8,9,"&nbsp;&nbsp;Member Id Report&nbsp;&nbsp;",n,n,"",n,n,n,0,0,0,1,1,1,"",n,n,n,n,n);
var s2=it.addSubmenu(0,0,-4,0,0,0,0,1,10,n,"",n,1,0,1,0,n,n,100);
it=s2.addItemWithImages(7,8,9,"&nbsp;&nbsp;Single Line&nbsp;&nbsp;",n,n,"",n,n,n,0,0,0,1,1,1,"",n,n,n,n,n);
var s3=it.addSubmenu(0,0,-4,0,0,0,0,6,5,n,"",n,1,0,1,0,n,n,100);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Small Font&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('mnum', '1', 'no', '', '0')",n,n);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Large Font&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('mnum', '2', 'no', '', '0')",n,n);
it=s2.addItemWithImages(7,8,9,"&nbsp;&nbsp;Double Line&nbsp;&nbsp;",n,n,"",n,n,n,0,0,0,1,1,1,"",n,n,n,n,n);
var s3=it.addSubmenu(0,0,-4,0,0,0,0,6,5,n,"",n,1,0,1,0,n,n,100);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Small Font&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('mnum', '1', 'no', '', '1')",n,n);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Large Font&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('mnum', '2', 'no', '', '1')",n,n);
it=s2.addItem(7,8,9,"&nbsp;&nbsp;Excel Format&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('mnum', '2', 'yes', '', '0')",n,n);
it=s1.addItemWithImages(7,8,9,"&nbsp;&nbsp;Alphabetical Lists&nbsp;&nbsp;",n,n,"",n,n,n,0,0,0,1,1,1,"",n,n,n,n,n);
var s2=it.addSubmenu(0,0,-4,0,0,0,0,6,5,n,"",n,1,0,1,0,n,n,100);
it=s2.addItemWithImages(7,8,9,"&nbsp;&nbsp;List By Member&nbsp;&nbsp;",n,n,"",n,n,n,0,0,0,1,1,1,"",n,n,n,n,n);
var s3=it.addSubmenu(0,0,-4,0,0,0,0,1,10,n,"",n,1,0,1,0,n,n,100);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Small Font&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('alpha', '1', 'no', '', '0')",n,n);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Large Font&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('alpha', '2', 'no', '', '0')",n,n);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Excel Format&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('alpha', '2', 'yes', '', '0')",n,n);
it=s2.addItem(7,8,9,"&nbsp;&nbsp;List By Member & Times&nbsp;&nbsp;",n,n,"","",n,n,n,n,n);
var s3=it.addSubmenu(0,0,-4,0,0,0,0,6,5,n,"",n,1,0,1,0,n,n,100);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Small Font&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('alphat', '1', 'no', '', '0')",n,n);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Large Font&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('alphat', '2', 'no', '', '0')",n,n);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Excel Format&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('alphat', '2', 'yes', '', '0')",n,n);
it=s2.addItemWithImages(7,8,9,"&nbsp;&nbsp;List By Mode Of Trans&nbsp;&nbsp;",n,n,"",n,n,n,0,0,0,1,1,1,"",n,n,n,n,n);
var s3=it.addSubmenu(0,0,-4,0,0,0,0,1,10,n,"",n,1,0,1,0,n,n,100);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Small Font&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('alpha', '1', 'no', 'trans', '0')",n,n);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Large Font&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('alpha', '2', 'no', 'trans', '0')",n,n);
it=s3.addItem(7,8,9,"&nbsp;&nbsp;Excel Format&nbsp;&nbsp;",n,n,"","",n,n,"exeControlPanelAction('alpha', '2', 'yes', 'trans', '0')",n,n);
it=s1.addItem(7,8,9,"&nbsp;&nbsp;List All Notes For This Day&nbsp;&nbsp;",n,n,"Create a report that will list all tee times that contain notes (includes the notes).","",n,n,"exeControlPanelAction('notes', '2', 'no', '', '0')",n,n);
it=s1.addItem(7,8,9,"&nbsp;&nbsp;How To Print In Color&nbsp;&nbsp;",n,n,"Clcik here to learn how to change settings to enable color printing","",n,n,n,"/v5/proshop_cpColor.htm","new");
s0.pm.buildMenu();
}}
