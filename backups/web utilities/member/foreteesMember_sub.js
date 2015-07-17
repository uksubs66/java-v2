//----------DHTML Menu Created using AllWebMenus PRO ver 3.1-#532---------------
//C:\JAVA\Tomcat\webapps\v5\web utilities\member\ForeteesMember.awm
var awmMenuName='foreteesMember_sub';
var awmLibraryBuild=532;
var awmLibraryPath='/awmData-foreteesMember';
var awmImagesPath='/awmData-foreteesMember';
var awmSupported=(navigator.appName + navigator.appVersion.substring(0,1)=="Netscape5" || document.all || document.layers || navigator.userAgent.indexOf('Opera')>-1)?1:0;
if (awmAltUrl!='' && !awmSupported) window.location.replace(awmAltUrl);
if (awmSupported){
var awmMenuPath;
if (document.layers) mpi=((document.images['awmMenuPathImg-foreteesMember_sub'])?document.images['awmMenuPathImg-foreteesMember_sub'].src:document.layers['xawmMenuPathImg-foreteesMember_sub'].document.images['awmMenuPathImg-foreteesMember_sub'].src); else mpi=document.images['awmMenuPathImg-foreteesMember_sub'].src;
awmMenuPath=mpi.substring(0,mpi.length-16);
while (awmMenuPath.search("'")>-1) {awmMenuPath=awmMenuPath.replace("'", "&#39;");}
var nua=navigator.userAgent,scriptNo=(nua.indexOf('Safari')>-1)?7:(nua.indexOf('Gecko')>-1)?2:((document.layers)?3:((nua.indexOf('Opera')>-1)?4:((nua.indexOf('Mac')>-1)?5:((nua.indexOf('Konqueror')>-1)?6:1))));
document.write("<SCRIPT SRC='"+awmMenuPath+awmLibraryPath+"/awmlib"+scriptNo+".js'><\/SCRIPT>");
var n=null;
awmzindex=1000;
}

var awmSubmenusFrame='';
var awmSubmenusFrameOffset=0;
var awmOptimize=1;
var awmforeteesMember_sub_1,awmforeteesMember_sub_2,awmforeteesMember_sub_4,awmforeteesMember_sub_5;
function awmBuildMenu(){if(awmSupported){
awmMenuName='foreteesMember_sub_1';
awmCreateCSS(1,2,0,'#FFFFFF','#2F2F2F',n,'bold 14px sans-serif',n,'none',1,n,0,1);
awmCreateCSS(0,1,0,n,'#CCCCAA',n,n,n,'solid',1,'#336633',0,0);
awmCreateCSS(1,2,0,'#336633','#CCCCAA',n,'bold 11px sans-serif',n,'solid',1,'#CCCCAA',0,1);
awmCreateCSS(0,2,0,'#000000','#F5F5DC',n,'bold 11px sans-serif',n,'solid',2,'#F5F5DC',0,1);
awmCreateCSS(0,2,0,'#800040','#F5F5DC',n,'bold 11px sans-serif',n,'solid',2,'#F5F5DC',0,1);
awmCreateCSS(1,2,0,'#336633','#CCCCAA',n,'bold 11px sans-serif',n,'none',1,'#CCCCAA',0,1);
var s0=awmCreateMenu(1,0,-1,0,0,0,0,1,0,0,0,0,0,1,0,n,"",n,1,0,1,0,"","",100);
awmforeteesMember_sub_1=s0;
it=s0.addItem(2,3,4,"&nbsp;&nbsp;Make, Change or View Tee Times&nbsp;&nbsp;",n,n,"View Tee sheets, make or change a tee time.","",n,n,"exeMenuAction('servlet/Member_select', 'get')",n,n);
it=s0.addItem(2,3,4,"&nbsp;&nbsp;Today's Tee Sheet&nbsp;&nbsp;",n,n,"View today&#39;s tee sheet.","",n,n,"exeMenuAction('servlet/Member_sheet?index=0', 'post')",n,n);
it=s0.addItem(5,3,4,"&nbsp;&nbsp;My Tee Times / Calendar&nbsp;&nbsp;",n,n,"View all your scheduled activities.","",n,n,"exeMenuAction('servlet/Member_teelist', 'get')",n,n);
it=s0.addItem(2,3,4,"&nbsp;&nbsp;My Tee Times / List&nbsp;&nbsp;",n,n,"","",n,n,"exeMenuAction('servlet/Member_teelist_list', 'get')",n,n);
s0.pm.buildMenu();
awmMenuName='foreteesMember_sub_2';
awmCreateCSS(0,1,0,n,'#CCCCAA',n,n,n,'solid',1,'#336633',0,0);
awmCreateCSS(1,2,0,'#336633','#CCCCAA',n,'bold 11px sans-serif',n,'none',1,'#CCCCAA',0,1);
awmCreateCSS(0,2,0,'#000000','#F5F5DC',n,'bold 11px sans-serif',n,'solid',2,'#F5F5DC',0,1);
awmCreateCSS(0,2,0,'#800040','#F5F5DC',n,'bold 11px sans-serif',n,'solid',2,'#F5F5DC',0,1);
var s0=awmCreateMenu(1,0,-1,0,0,0,0,1,0,0,0,0,0,0,n,n,"",n,1,0,1,0,"awmhidediv();","awmshowdiv();",100);
awmforeteesMember_sub_2=s0;
it=s0.addItem(1,2,3,"&nbsp;&nbsp;Individual Lessons&nbsp;&nbsp;",n,n,"","",n,n,"exeMenuAction('servlet/Member_lesson', 'post')",n,n);
it=s0.addItem(1,2,3,"&nbsp;&nbsp;Group Lessons&nbsp;&nbsp;",n,n,"","",n,n,"exeMenuAction('servlet/Member_lesson?group=yes', 'post')",n,n);
it=s0.addItem(1,2,3,"&nbsp;&nbsp;View Pros' Bios&nbsp;&nbsp;",n,n,"","",n,n,"exeMenuAction('servlet/Member_lesson?bio=yes', 'post')",n,n);
s0.pm.buildMenu();
awmMenuName='foreteesMember_sub_4';
awmCreateCSS(0,1,0,n,'#CCCCAA',n,n,n,'solid',1,'#336633',0,0);
awmCreateCSS(1,2,0,'#336633','#CCCCAA',n,'bold 11px sans-serif',n,'none',1,'#CCCCAA',0,1);
awmCreateCSS(0,2,0,'#000000','#F5F5DC',n,'bold 11px sans-serif',n,'solid',2,'#F5F5DC',0,1);
awmCreateCSS(0,2,0,'#800040','#F5F5DC',n,'bold 11px sans-serif',n,'solid',2,'#F5F5DC',0,1);
var s0=awmCreateMenu(1,0,-1,0,0,0,0,1,0,0,0,0,0,0,n,n,"",n,1,0,1,0,"awmhidediv();","awmshowdiv();",100);
awmforeteesMember_sub_4=s0;
it=s0.addItem(1,2,3,"&nbsp;&nbsp;Other Members' Tee Times&nbsp;&nbsp;",n,n,"Search for other members&#39; tee times.","",n,n,"exeMenuAction('servlet/Member_searchmem', 'get')",n,n);
it=s0.addItem(1,2,3,"&nbsp;&nbsp;Your Past Tee Times - This Calendar Year&nbsp;&nbsp;",n,n,"Search for your past tee times during this calendar year.","",n,n,"exeMenuAction('servlet/Member_searchpast?subtee=cal', 'post')",n,n);
it=s0.addItem(1,2,3,"&nbsp;&nbsp;Your Past Tee Times - Past 12 Months&nbsp;&nbsp;",n,n,"Search for your past tee times during the past 12 months..","",n,n,"exeMenuAction('servlet/Member_searchpast?subtee=year', 'post')",n,n);
it=s0.addItem(1,2,3,"&nbsp;&nbsp;Your Past Tee Times - Since Inception&nbsp;&nbsp;",n,n,"Search for your past tee times since the inception of ForeTees.","",n,n,"exeMenuAction('servlet/Member_searchpast?subtee=forever', 'post')",n,n);
s0.pm.buildMenu();
awmMenuName='foreteesMember_sub_5';
awmCreateCSS(0,1,0,n,'#CCCCAA',n,n,n,'solid',1,'#336633',0,0);
awmCreateCSS(1,2,0,'#336633','#CCCCAA',n,'bold 11px sans-serif',n,'solid',1,'#CCCCAA',0,1);
awmCreateCSS(0,2,0,'#000000','#F5F5DC',n,'bold 11px sans-serif',n,'solid',2,'#F5F5DC',0,1);
awmCreateCSS(0,2,0,'#800040','#F5F5DC',n,'bold 11px sans-serif',n,'solid',2,'#F5F5DC',0,1);
var s0=awmCreateMenu(1,0,-1,0,0,0,0,1,0,0,0,0,0,0,n,n,"",n,1,0,1,0,"awmhidediv();","awmshowdiv();",100);
awmforeteesMember_sub_5=s0;
it=s0.addItem(1,2,3,"&nbsp;&nbsp;Send Email&nbsp;&nbsp;",n,n,"Send Email messages to other members.","",n,n,"exeMenuAction('servlet/Send_email', 'get')",n,n);
it=s0.addItem(1,2,3,"&nbsp;&nbsp;View Distribution Lists&nbsp;&nbsp;",n,n,"View your current distribution lists.","",n,n,"exeMenuAction('servlet/Communication', 'get')",n,n);
it=s0.addItem(1,2,3,"&nbsp;&nbsp;Add Distribution List&nbsp;&nbsp;",n,n,"Add other members to a distribution list for sending emails to a group.","",n,n,"exeMenuAction('servlet/Add_distributionlist','get')",n,n);
s0.pm.buildMenu();}}
