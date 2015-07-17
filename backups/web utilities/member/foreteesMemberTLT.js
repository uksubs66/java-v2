//----------DHTML Menu Created using AllWebMenus PRO ver 3.1-#532---------------
//C:\JAVA\Tomcat\webapps\v5\web utilities\member\ForeteesMember.awm
var awmMenuName='foreteesMember';
var awmLibraryBuild=532;
var awmLibraryPath='/awmData-foreteesMemberTLT';
var awmImagesPath='/awmData-foreteesMemberTLT';
var awmSupported=(navigator.appName + navigator.appVersion.substring(0,1)=="Netscape5" || document.all || document.layers || navigator.userAgent.indexOf('Opera')>-1)?1:0;
if (awmAltUrl!='' && !awmSupported) window.location.replace(awmAltUrl);
if (awmSupported){
var awmMenuPath;
if (document.layers) mpi=((document.images['awmMenuPathImg-foreteesMemberTLT'])?document.images['awmMenuPathImg-foreteesMemberTLT'].src:document.layers['xawmMenuPathImg-foreteesMemberTLT'].document.images['awmMenuPathImg-foreteesMemberTLT'].src); else mpi=document.images['awmMenuPathImg-foreteesMemberTLT'].src;
awmMenuPath=mpi.substring(0,mpi.length-16);
while (awmMenuPath.search("'")>-1) {awmMenuPath=awmMenuPath.replace("'", "&#39;");}
var nua=navigator.userAgent,scriptNo=(nua.indexOf('Safari')>-1)?7:(nua.indexOf('Gecko')>-1)?2:((document.layers)?3:((nua.indexOf('Opera')>-1)?4:((nua.indexOf('Mac')>-1)?5:((nua.indexOf('Konqueror')>-1)?6:1))));
document.write("<SCRIPT SRC='"+awmMenuPath+awmLibraryPath+"/awmlib"+scriptNo+".js'><\/SCRIPT>");
var n=null;
awmzindex=1000;
}

var awmSubmenusFrame='bot';
var awmSubmenusFrameOffset;
var awmOptimize=1;
function awmBuildMenu(){
if (awmSupported){
awmImagesColl=['Mem_Notifications.gif',100,24,'Mem_Lessons.gif',75,24,'Mem_Events.gif',50,24,'Mem_Search.gif',66,24,'Mem_Email.gif',59,24,'Mem_Partners.gif',59,24,'Mem_Settings.gif',58,24];
awmCreateCSS(1,2,0,'#FFFFFF','#2F2F2F',n,'bold 14px sans-serif',n,'none',1,'#00FF40',0,1);
awmCreateCSS(0,1,0,n,'#CCCCAA',n,n,n,'solid',1,'#CCCCAA',0,0);
awmCreateCSS(1,2,0,'#000080','#CCCCAA',n,'14px serif',n,'none',1,'#CCCCAA',0,1);
awmCreateCSS(0,2,0,'#800040','#CCCCAA',n,'14px serif',n,'none',2,'#CCCCAA',0,1);
var s0=awmCreateMenu(0,0,0,0,1,0,1,1,7,0,0,0,1,1,0,n,"",n,1,1,0,1,"","",100,0);
it=s0.addItemWithImages(2,3,3,"","","","Golf Notifications",0,0,0,0,0,0,n,n,n,"",n,n,n,n,n);
it=s0.addItemWithImages(2,3,3,"","","","Schedule a lesson with a golf professional.",1,1,1,0,0,0,n,n,n,"",n,n,n,n,n);
it=s0.addItemWithImages(2,3,3,"","","","View upcoming events or sign up for an event.",2,2,2,0,0,0,n,n,n,"",n,n,"exeMenuAction('servlet/Member_events', 'get')",n,n);
it=s0.addItemWithImages(2,3,3,"","","","Seach for members",3,3,3,0,0,0,n,n,n,"",n,n,n,n,n);
it=s0.addItemWithImages(2,3,3,"","","","Send an email.",4,4,4,0,0,0,n,n,n,"",n,n,n,n,n);
it=s0.addItemWithImages(2,3,3,"","","","Maintain your buddy list.",5,5,5,0,0,0,n,n,n,"",n,n,"exeMenuAction('servlet/Member_buddy', 'get')",n,n);
it=s0.addItemWithImages(2,3,3,"","","","Maintain your personal configuration settings (email address, mode of transportation, etc.).",6,6,6,0,0,0,n,n,n,"",n,n,"exeMenuAction('servlet/Member_services', 'get')",n,n);
s0.pm.buildMenu();
}}
