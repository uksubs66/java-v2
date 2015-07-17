//---- Created using Likno Web Modal Windows Builder ver. 1.1.128.1 -----

// Copyright (c) Likno Software 2008-2009
// This code is property of Likno Software and licensed for use in *websites* only. It is *not* licensed for use in distributable implementations (applications or CD-based webs), unless the related license is provided by Likno Software.
// Any unauthorized use, reverse-engineering, alteration, transmission, transformation, or copying of any means (electronic or not) is strictly prohibited and will be prosecuted.
// *Removal of the present copyright notice is strictly prohibited*

var headID = document.getElementsByTagName("head")[0];

var lwmwName = 'foretees-modal';

var nua=navigator.userAgent,scriptNo=(nua.indexOf('Chrome')>-1)?2:((nua.indexOf('Safari')>-1)?7:(nua.indexOf('Gecko')>-1)?2:((document.layers)?3:((nua.indexOf('Opera')>-1)?4:((nua.indexOf('Mac')>-1)?5:1))));var lwmwmpi=document.location,xt="";
var mpa=lwmwmpi.protocol+"//"+lwmwmpi.host;
var lwmwmpi=lwmwmpi.protocol+"//"+lwmwmpi.host+lwmwmpi.pathname;
if(scriptNo==1){oBC=document.all.tags("BASE");if(oBC && oBC.length) if(oBC[0].href) lwmwmpi=oBC[0].href;}
while (lwmwmpi.search(/\\/)>-1) lwmwmpi=lwmwmpi.replace("\\","/");
lwmwmpi=lwmwmpi.substring(0,lwmwmpi.lastIndexOf("/")+1);
var e=document.getElementsByTagName("SCRIPT");
for (var i=0;i<e.length;i++){if (e[i].src){if (e[i].src.indexOf(lwmwName+".js")!=-1){xt=e[i].src.split("/");if (xt[xt.length-1]==lwmwName+".js"){xt=e[i].src.substring(0,e[i].src.length-lwmwName.length-3);if (e[i].src.indexOf("://")!=-1){lwmwmpi=xt;}else{if(xt.substring(0,1)=="/")lwmwmpi=mpa+xt; else lwmwmpi+=xt;}}}}}
while (lwmwmpi.search(/\/\.\//)>-1) {lwmwmpi=lwmwmpi.replace("/./","/");}

var foretees_modal,loadmodal;

if (typeof(jQuery) == 'undefined'){
	var newScript_jQ = document.createElement('script');
	newScript_jQ.type = 'text/javascript';
	newScript_jQ.onload = jQloaded;
	newScript_jQ.onreadystatechange = jQloaded;
	newScript_jQ.src = lwmwmpi+"jquery.js";
	headID.appendChild(newScript_jQ);
} else jQloaded();

function jQloaded(){
	if (typeof(jQuery) != 'undefined'){ 
		var newScript_lib = document.createElement('script');
		newScript_lib.type = 'text/javascript';
		newScript_lib.src = lwmwmpi+"likno-modal-lib.js";
		newScript_lib.onload = load_foretees_modal;
		newScript_lib.onreadystatechange = load_foretees_modal;
		headID.appendChild(newScript_lib);
	}
}

function load_foretees_modal() {if (typeof(jQuery) != 'undefined' && typeof(jQuery.liknoModal) != 'undefined'){jQuery.foretees_modal = jQuery.extend(true, {}, jQuery.liknoModal);jQuery.foretees_modal.impl.defaults = jQuery.extend(true, jQuery.foretees_modal.impl.defaults, {modalClass: 'foretees_modal',overlayId: 'foretees_modal-overlay',containerId: 'foretees_modal-container',ZIndex:2000,overlayCss:{backgroundColor:'#000000'},opacity:50,close:{closeClass: 'foretees-modal-close',overlayClose:false,escClose:false,show:false},containerCss:nRTC("width:700px;height:450px;font-family:Verdana,Arial,Helvetica,sans-serif; font-size:11px;"),domainCode:'246C373D5B223638373437343730222C22334132463246222C223233373536453643363936443639373436353634333132333246225D2C246C352C246C363D5B223636363936433635222C223643364636333631364336383646373337343246222C223233373536453643363936443639373436353634333132333246225D3B',ajaxLoadDiv:"<div id='modalAjax' style='background-image:url("+lwmwmpi+"ajax-loader-clock.gif);background-position: center center; background-repeat: no-repeat;'></div>",header:{show:false},footer:{show:false},wrapCss:nRTC("font-family:Tahoma, Arial, Helvetica, sans-serif; font-size:11px; background-color:#CCCCAA; border:4px solid #8B8970;padding:20px;color:#cccccc;line-height:18px;text-align:justify;"),isMultiSheet:false, currentSheet:0,loop:true,play:{autoStart:false,pauseByMouse:false,direction:0,delay:1500},navigationBar:{maxSheets:9,show:true,showFirst:true,showPrev:true,showPlayPause:true,showNext:true,showLast:true,showSheets:true,cssClasses:".foretees_modal{border-width:0px;margin:0px;padding:0px;}",text:"Sheets:", images:{butFirst:lwmwmpi+'image_064_first.png',butFirstOver:lwmwmpi+'image_064_first_over.png',butPrev:lwmwmpi+'image_064_previous.png',butPrevOver:lwmwmpi+'image_064_previous_over.png',butPlay:lwmwmpi+'image_064_play.png',butPlayOver:lwmwmpi+'image_064_play_over.png',butPause:lwmwmpi+'image_064_pause.png',butPauseOver:lwmwmpi+'image_064_pause_over.png',butNext:lwmwmpi+'image_064_next.png',butNextOver:lwmwmpi+'image_064_next_over.png',butLast:lwmwmpi+'image_064_last.png',butLastOver:lwmwmpi+'image_064_last_over.png'}},transition:{openWhat:{height:'show'}, openHow: 0,closeWhat:{height:'hide'}, closeHow: 0},dataId: "foretees_modal-data"});jQuery(function () {jQuery("#loadmodal").click(function(){loadmodal();});});foretees_modal = function (data, options) {return jQuery.foretees_modal.impl.init(data, options);};foretees_modal.close = function (doNext) {return jQuery.foretees_modal.impl.close(doNext);};foretees_modal.loadmodal=loadmodal=function(options){jQuery.foretees_modal.impl.init("<iframe id=\"modaliframe\" src=\"/v5/servlet/Common_guestdb?modal\" width=\"650px\" valign=\"center\" align=\"center\" scrolling=no frameborder=no></iframe> ",options);};}}
/*128.1*/