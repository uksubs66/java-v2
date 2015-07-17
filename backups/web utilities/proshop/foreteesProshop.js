//----------DHTML Menu Created using AllWebMenus PRO ver 3.1-#532---------------
//C:\JAVA\Tomcat\webapps\v5\web utilities\proshop\ForeteesProshop.awm
var awmMenuName='foreteesProshop';
var awmLibraryBuild=532;
var awmLibraryPath='/awmData-foreteesProshop';
var awmImagesPath='/awmData-foreteesProshop';
var awmSupported=(navigator.appName + navigator.appVersion.substring(0,1)=="Netscape5" || document.all || document.layers || navigator.userAgent.indexOf('Opera')>-1)?1:0;
if (awmAltUrl!='' && !awmSupported) window.location.replace(awmAltUrl);
if (awmSupported){
var awmMenuPath;
if (document.layers) mpi=((document.images['awmMenuPathImg-foreteesProshop'])?document.images['awmMenuPathImg-foreteesProshop'].src:document.layers['xawmMenuPathImg-foreteesProshop'].document.images['awmMenuPathImg-foreteesProshop'].src); else mpi=document.images['awmMenuPathImg-foreteesProshop'].src;
awmMenuPath=mpi.substring(0,mpi.length-16);
while (awmMenuPath.search("'")>-1) {awmMenuPath=awmMenuPath.replace("'", "&#39;");}
var nua=navigator.userAgent,scriptNo=(nua.indexOf('Safari')>-1)?7:(nua.indexOf('Gecko')>-1)?2:((document.layers)?3:((nua.indexOf('Opera')>-1)?4:((nua.indexOf('Mac')>-1)?5:((nua.indexOf('Konqueror')>-1)?6:1))));
document.write("<SCRIPT SRC='"+awmMenuPath+awmLibraryPath+"/awmlib"+scriptNo+".js'><\/SCRIPT>");
var n=null;
awmzindex=100;
}

var awmSubmenusFrame='bot';
var awmSubmenusFrameOffset;
var awmOptimize=1;
function awmBuildMenu(){
if (awmSupported){
awmImagesColl=['ProTee_Sheets.gif',85,24,'ProTee_Sheets-over.gif',85,24,'Event_Sign_Up.gif',92,24,'Event_Sign_Up-over.gif',91,24,'ProLessons.gif',71,24,'ProLessons-over.gif',71,24,'ProLottery.gif',64,24,'ProLottery-over.gif',63,24,'ProReports.gif',68,24,'ProReports-over.gif',67,24,'ProTools.gif',53,24,'ProTools-over.gif',54,24,'ProSystem_Config.gif',107,24,'ProSystem_Config-over.gif',106,24];
awmCreateCSS(1,2,0,'#FFFFFF','#2F2F2F',n,'bold 14px sans-serif',n,'none',1,'#00FF40',0,1);
awmCreateCSS(0,1,0,n,'#CCCCAA',n,n,n,'solid',1,'#CCCCAA',0,0);
awmCreateCSS(1,2,0,'#336633','#CCCCAA',n,'bold 11px sans-serif',n,'none',1,'#CCCCAA',0,1);
awmCreateCSS(0,2,0,'#000000','#CCCCAA',n,'bold 11px sans-serif',n,'none',2,'#CCCCAA',0,1);
awmCreateCSS(0,2,0,'#800040','#CCCCAA',n,'bold 11px sans-serif',n,'none',2,'#CCCCAA',0,1);
var s0=awmCreateMenu(0,0,0,0,3,0,1,1,7,0,0,0,1,1,0,n,"",n,1,1,0,1,"","",100,0);
it=s0.addItemWithImages(2,3,4,"","","","",0,1,1,0,0,0,n,n,n,"",n,n,n,n,n);
it=s0.addItemWithImages(2,3,4,"","","","Process Event Registrations",2,3,3,0,0,0,n,n,n,"",n,n,"exeMenuAction('servlet/Proshop_events1', 'post')",n,n);
it=s0.addItemWithImages(2,3,4,"","","","",4,5,5,0,0,0,n,n,n,"",n,n,n,n,n);
it=s0.addItemWithImages(2,3,4,"","","","",6,7,7,0,0,0,n,n,n,"",n,n,n,n,n);
it=s0.addItemWithImages(2,3,4,"","","","",8,9,9,0,0,0,n,n,n,"",n,n,n,n,n);
it=s0.addItemWithImages(2,3,4,"","","","",10,11,11,0,0,0,n,n,n,"",n,n,n,n,n);
it=s0.addItemWithImages(2,3,4,"","","","",12,13,13,0,0,0,n,n,n,"",n,n,n,n,n);
s0.pm.buildMenu();
}}
