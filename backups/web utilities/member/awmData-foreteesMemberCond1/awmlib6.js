//****** AllWebMenus Libraries Version # 532 ******

// Copyright (c) Likno Software 2000-2005
// The present javascript code is property of Likno Software.
// This code can only be used inside Internet/Intranet web sites located on *web servers*, as the outcome of a licensed AllWebMenus application only. 
// This code *cannot* be used inside distributable implementations (such as demos, applications or CD-based webs), unless this implementation is licensed with an "AllWebMenus License for Distributed Applications". 
// Any unauthorized use, reverse-engineering, alteration, transmission, transformation, facsimile, or copying of any means (electronic or not) is strictly prohibited and will be prosecuted.
// ***Removal of the present copyright notice is strictly prohibited***

var awmhd=200,awmDefaultStatusbarText="",is60=(navigator.userAgent.indexOf("6.0")!=-1),n=null,awmcrm,awmcre,awmmo,awmso,awmctm=n,awmuc,awmud,awmctu,awmun,awmdid,awmsht="",awmsoo=0,awmKonOffsetX,awmKonOffsetY;var awmliw=window.innerWidth,awmlih=window.innerHeight,awmlsx=window.pageXOffset,awmlsy=window.pageYOffset,awmalt=["left","center","right"],awmplt=["absolute","relative"],awmvlt=["visible","hidden","inherit"],awmctlt=["default","pointer","crosshair","help","move","text","wait"];if (awmso>0){awmsoo=awmso+1;}else  {var awmsc=new Array();}var awmlssx=window.pageXOffset;var awmlssy=window.pageYOffset;var awmSelectedItem;var awmRightToLeftFrame;if (!awmun) awmun=0;if (awmcre>=0 && typeof(awmcre)!='undefined'); else  awmcre=0;window.onunload=awmwu;window.onresize = awmwr;
function awmhidediv(){var m=1;while (document.getElementById("awmflash"+m)){document.getElementById("awmflash"+m).style.visibility="hidden";m++;}}
function awmshowdiv(){var m=1;while (document.getElementById("awmflash"+m)){document.getElementById("awmflash"+m).style.visibility="visible";m++;}}
function awmiht (image){return "<img src='"+awmMenuPath+awmImagesPath+"/"+awmImagesColl[image*3]+"' width="+awmImagesColl[image*3+1]+" height="+awmImagesColl[image*3+2]+" align=absmiddle>";}
function awmatai (text,image,algn){if (text==null) text="";var replaceString = / /;while (text.search(replaceString)>-1){text=text.replace(replaceString, "&nbsp;");}var s1=(text!="" && text!=null && (algn==0 || algn==2) && image!=null)?"<br>":"";var s2=(image!=n)?awmiht (image):"";return "<nobr>"+((algn==0 || algn==3)?s2+s1+text:text+s1+s2)+"</nobr>";}
function awmCreateCSS (pos,vis,algnm,fgc,bgc,bgi,fnt,tdec,bs,bw,bc,pd,crs){if (awmso>=0 && typeof(awmso)!='undefined') awmso++; else  awmso=0;var style={ id:"AWMST"+awmso,id2:"AWMSTTD"+awmso,pos:pos,vis:vis,algnm:algnm,fgc:fgc,bgc:bgc,bgi:bgi,fnt:fnt,tdec:tdec,bs:bs,bw:bw,bc:bc,zi:awmzindex,pd:pd,crs:crs};awmsht+="."+style.id+" {position:absolute; visibility:"+awmvlt[vis]+"; "+"text-align:"+awmalt[algnm]+"; "+((fnt!=n)?"font:"+fnt+"; ":"")+((tdec!=n)?"text-decoration:"+tdec+"; ":"")+((fgc!=n)?"color:"+fgc+"; ":"")+"background-color:"+((bgc!=n)?bgc+"; ":"transparent; ")+((bgi!=n)?"background-image:url('"+awmMenuPath+awmImagesPath+"/"+awmImagesColl[bgi*3]+"'); ":"")+((bs!=n)?"border-style:"+bs+"; ":"")+((bw!=0)?"border-width:"+bw+"; ":"")+((bc!=n)?"border-color:"+bc+"; ":"")+" cursor:"+awmctlt[crs]+"; z-index:"+style.zi+"}";awmsht+="."+style.id2+" {border-style:none;border-width:0px;text-align:"+awmalt[algnm]+"; "+((fnt!=n)?"font:"+fnt+"; ":"")+((tdec!=n)?"text-decoration:"+tdec+"; ":"")+((fgc!=n)?"color:"+fgc+"; ":"")+"background-color:"+((bgc!=n && bgi==n)?bgc+"; ":"transparent; ")+"}";awmsc[awmsc.length]=style;}
function awmCreateMenu (cll,swn,swr,mh,ud,sa,mvb,dft,crn,dx,dy,ss,ct,cs,ts,tn,ttt,ti,tia,dbi,ew,eh,jcoo,jcoc){if (awmmo>=0 && typeof(awmmo)!='undefined') awmmo++; else  {awmm=new Array(); awmmo=0};var me={ ind:awmmo,nm:awmMenuName,cn:new Array(),fl:!awmsc[cs].pos,cll:cll,mvb:mvb,dft:dft,crn:crn,dx:(ct<2)?dx:0,dy:dy,ss:ss,sht:"<STYLE>.awmGeneric{background-color:transparent}"+awmsht+"</STYLE>",rep:0,mio:0,st:awmOptimize?2:3,submenusFrameOffset:awmSubmenusFrameOffset,selectedItem:(typeof(awmSelectedItem)=='undefined')?0:awmSelectedItem,offX:(awmKonOffsetX)?awmKonOffsetX:0,offY:(awmKonOffsetY)?awmKonOffsetY:0,addSubmenu:awmas,ght:awmmght,whtd:awmmwhttd,buildMenu:awmbmm,cm:awmmcm};me.pm=me;me.addSubmenu(ct,swn,swr,mh,ud,sa,1,cs,ts,tn,ttt,ti,tia,dbi,ew,eh,jcoo,jcoc);if (typeof(awmRelativeCorner)=='undefined'){me.rc=0} else  {me.rc=awmRelativeCorner;awmRelativeCorner=0}me.cn[0].pi=null;if (mvb) document.onmousemove=awmotmm;awmm[awmmo]=me;awmsht="";return me.cn[0];}
function awmas (ct,swn,swr,shw,ud,sa,od,cs,ts,tn,ttt,ti,tia,dbi,ew,eh,jcoo,jcoc){cnt={ id:"AWMEL"+(awmcre++),it:new Array(),tid:"AWMEL"+(awmcre++),ct:ct,swn:swn,swr:swr,shw:(shw>2)?2:shw,ud:ud,sa:sa,od:od,cs:awmsc[cs+awmsoo],ts:(ts!=null)?awmsc[ts+awmsoo]:null,tn:tn,ttt:ttt,ti:ti,ht:(tn!=null || ti!=null),tia:tia,dbi:dbi,ew:ew,eh:eh,jcoo:jcoo,jcoc:jcoc,pi:this,pm:this.pm,pm:this.pm,siw:0,wtd:false,argd:0,ft:0,mio:0,hsid:null,uid:null,dox:0,doy:0,addItem:awmai,addItemWithImages:awmaiwi,show:awmcs,fe:awmcfe,arr:awmca,ght:awmcght,pc:awmpc,unf:awmcu,hdt:awmchdt,onmouseover:awmocmo,onmouseout:awmocmot,otmd:awmotmd,otmu:awmotmu,otmm:awmotmm};this.sm=cnt;cnt.pm.cn[cnt.ind=cnt.pm.cn.length]=cnt;cnt.cd=(cnt.ind==0 && cnt.pm.cll==0)?0:1;return cnt;}
function awmai (st0,st1,st2,in0,in1,in2,tt,sbt,jc0,jc1,jc2,url,tf,minWidth,minHeight){var itm={ id:"AWMEL"+(awmcre++),style:[(st0==n)?n:awmsc[st0+awmsoo],(st1==n)?n:awmsc[st1+awmsoo],(st2==n)?n:awmsc[st2+awmsoo]],inm:[in0,(in1==n)?in0:in1,(in2==n)?in0:in2],ii:[n,n,n],ia:[n,n,n],hsi:[n,n,n],tt:tt,sbt:sbt,jc:[jc0,jc1,jc2],tf:tf,top:0,left:0,layer:[n,n,n],ps:this,pm:this.pm,sm:null,minHeight:(minHeight)?minHeight:0,minWidth:(minWidth)?minWidth:0,ght:awmight,shst:awmiss,addSubmenu:awmas,onmouseover:awmoimo,onmouseout:awmoimot,onmousedown:awmoimd,onmouseup:awmoimu};if (url!=null){var prf=url.substring(0,7);if ((prf!="http://") && (prf!="https:/") && (prf!="mailto:") && (prf!="file://") && (url.substring(0,9)!="outlook:/") && (url.substring(0,6)!="ftp://") && (url.substring(0,6)!="mms://") && (url.substring(0,1)!="/")) url=awmMenuPath+"/"+url;}itm.url=url;this.it[itm.ind=this.it.length]=itm;return itm;}
function awmaiwi (st0,st1,st2,in0,in1,in2,tt,ii0,ii1,ii2,ia0,ia1,ia2,hsi0,hsi1,hsi2,sbt,jc0,jc1,jc2,url,tf,minWidth,minHeight){var itm=this.addItem (st0,st1,st2,in0,in1,in2,tt,sbt,jc0,jc1,jc2,url,tf,minWidth,minHeight);itm.ii=[ii0,ii1,ii2];itm.ia=[ia0,ia1,ia2];itm.hsi=[hsi0,hsi1,hsi2];this.siw=Math.max(this.siw,Math.max(((hsi0!=n)?awmImagesColl[hsi0*3+1]:0),Math.max(((hsi1!=n)?awmImagesColl[hsi1*3+1]:0),((hsi2!=n)?awmImagesColl[hsi2*3+1]:0))));return itm;}
function awmmght(cnt){for (var cno=0; cno<this.cn.length; cno++)this.cn[cno].ght();}
function awmcght(){var is="",hct=" style='width=1000;height=1000;' ";hct="";if (this.pm.fl || this.pi!=null) hct=" style='left:-3000; top:-3000;' "; this.htx="<div id='"+this.id+"' class='"+this.cs.id+"'"+hct+" onMouseOver='this.prc.onmouseover();' onMouseOut='this.prc.onmouseout();'>";if (this.ht){var es="";var tst=this.ts;this.htx+="<table id='"+this.tid+"_0' title='"+this.ttt+"' class='"+this.ts.id+"' border='0' cellpadding='0' cellspacing='0'"+es+"><tr><td class='"+this.ts.id2+"' valign='middle' style='padding:"+tst.pd+"px; "+((tst.fnt!=n)?"font:"+tst.fnt+"; ":"")+((tst.tdec!=n)?"text-decoration:"+tst.tdec+"; ":"")+((tst.fgc!=n)?"color:"+tst.fgc+"; ":"")+"'>"+awmatai(this.tn,this.ti,this.tia)+"</td></tr></table>";}for (p=0; p<this.it.length; p++){this.htx+=this.it[p].ght();if (p<this.it.length-1) this.htx+=is;}this.htx+="</div>";return this.htx;}
function awmight(){var htx="";for (var q=0; q<this.pm.st; q++){var ist=this.style[q];htx+="<table id='"+this.id+"_"+q+"' title='"+this.tt+"' class='"+this.style[q].id+"' style='left:-3000;' border='0' cellpadding='0' cellspacing='0'><tr><td class='"+this.style[q].id2+"' valign='middle' style='padding:"+ist.pd+"px; "+((ist.fnt!=n)?"font:"+ist.fnt+"; ":"")+((ist.tdec!=n)?"text-decoration:"+ist.tdec+"; ":"")+((ist.fgc!=n)?"color:"+ist.fgc+"; ":"")+"'>"+awmatai(this.inm[q],this.ii[q],this.ia[q])+"</td>";if (this.ps.siw>0){htx+="<td class='"+this.style[q].id2+"' width='"+this.ps.siw+"'>";if (this.hsi[q]!=n) htx+=awmiht(this.hsi[q]);else  htx+="<span style='font-size:0;'>&nbsp;</span>";htx+="</td>";}htx+="</tr></table>";}htx+="<img id='"+this.id+"_4' title='"+this.tt+"' style='position:absolute; cursor:"+awmctlt[this.style[0].crs]+"; z-index:"+awmzindex+";' src='"+awmMenuPath+awmLibraryPath+"/dot.gif' onMouseOver='this.pi.onmouseover();' onMouseOut='this.pi.onmouseout();' onMouseDown='this.pi.onmousedown();'>";return htx;}
function awmmwhttd(){var s="",crc;document.write(this.sht);for (var i=0; i<this.cn.length; i++) document.write(this.cn[i].htx);}
function awmcfe(){if (this.ft) return;this.layer=document.getElementById(this.id);this.layer.prc=this;if (this.ht){this.tl=this.layer.childNodes[0];this.tl.prc=this;if (this.pm.mvb && this.pi==null){this.tl.onmousedown=awmotmd;this.tl.onmousemove=awmotmm;this.tl.onmouseup=awmotmu;}}var var1=(this.ht)?1:0;for (var p=0; p<this.it.length; p++){this.it[p].elr=this.layer.childNodes[(this.pm.st+1)*(p+1)+var1-1];this.it[p].elr.pi=this.it[p];this.it[p].elr.onmouseup=awmoimu;for (var q=0; q<this.pm.st; q++){this.it[p].layer[q]=this.layer.childNodes[p*(this.pm.st+1)+q+var1];this.it[p].layer[q].pi=this.it[p];}}this.ft=1;}
function awmca(){if (this.argd) return;var w, h, tw, th, iw, ih, mwt=0, mht=0, nl=0, nt=0;var wts=new Array(), hts=new Array();if (this.ht){this.tl.parentNode.setAttribute("style","width:1000px; height:1000px;");tw=this.tl.offsetWidth;th=this.tl.offsetHeight;mwt=tw;mht=th;}for (var p=0; p<this.it.length; p++){iw=this.it[p].minWidth+((this.it[p].style[0].bs=="none")?0:2*this.it[p].style[0].bw);ih=this.it[p].minHeight+((this.it[p].style[0].bs=="none")?0:2*this.it[p].style[0].bw-2);for (var q=this.pm.st-1; q>=0; q--){iw=Math.max(iw,this.it[p].layer[q].offsetWidth);ih=Math.max(ih,this.it[p].layer[q].offsetHeight);mwt=Math.max(iw,mwt);mht=Math.max(ih,mht);}wts[p]=iw;hts[p]=ih;}if (this.ht){w=(this.ew)?mwt:tw;h=(this.eh)?mht:th;this.tl.setAttribute("style","left:0px; top:0px; width:"+(w-((this.ts.bs=="none")?0:4*this.ts.bw))+"px; height:"+(h-((this.ts.bs=="none")?0:2*this.ts.bw))+"px;");this.tl.childNodes[0].childNodes[0].childNodes[0].setAttribute("style","left:0px; top:0px; width:"+(w-((this.ts.bs=="none")?0:4*this.ts.bw))+"px; height:"+(h-((this.ts.bs=="none")?0:2*this.ts.bw))+"px;");if (this.ct==0) nt+=h+this.dbi; else  nl+=w+this.dbi;}for (var p=0; p<this.it.length; p++){for (var q=0; q<this.pm.st; q++){w=(this.ew)?mwt:wts[p];h=(this.eh)?mht:hts[p];this.it[p].layer[q].setAttribute("style","left:"+nl+"px; top:"+nt+"px; width:"+(w-((this.it[p].style[q].bs=="none")?0:4*this.it[p].style[q].bw))+"px; height:"+(h-((this.it[p].style[q].bs=="none")?0:2*this.it[p].style[q].bw))+"px;");this.it[p].layer[q].childNodes[0].childNodes[0].childNodes[0].setAttribute("style","left:"+nl+"px; top:"+nt+"px; width:"+(w-((this.it[p].style[q].bs=="none")?0:2*this.it[p].style[q].bw))+"px; height:"+(h-((this.it[p].style[q].bs=="none")?0:4*this.it[p].style[q].bw))+"px;");this.it[p].layer[q].style.visibility=((q>0)?"hidden":"visible");}var els=this.it[p].elr.style;els.left=nl;els.top=nt;els.width=w;els.height=h;if (this.ct==0) nt+=h+this.dbi; else  nl+=w+this.dbi;}if (this.ct==0){this.layer.setAttribute("style","left:"+"0"+"px; top:"+"0"+"px; width:"+mwt+"px; height:"+(nt-this.dbi)+"px;");this.layer.style.width=mwt;this.layer.style.height=nt-this.dbi;} else  {this.layer.setAttribute("style","left:"+"0"+"px; top:"+"0"+"px; width:"+(nl-this.dbi)+"px; height:"+mht+"px;");this.layer.style.width=nl-this.dbi;this.layer.style.height=mht;}if (this.ct==2) this.layer.style.width=window.innerWidth-2*this.cs.bw;this.layer.style.borderStyle=this.cs.bs;this.layer.style.borderWidth=this.cs.bw;this.argd=1;}
function awmcs(sf,x,y){if (sf){this.cd=0;this.fe();this.arr();if (arguments.length==1) this.pc();else  {this.left=this.layer.style.left=x;this.top=this.layer.style.top=y;}this.layer.style.visibility="visible";if (this.shw>0 && !awmun) this.unf();if (this.jcoo!=null) eval("setTimeout(\""+this.jcoo+"\",10);");if (this.ind==0) if (this.pm.selectedItem>0) this.it[this.pm.selectedItem-1].shst(2);} else  {if (!this.ft || this.mio || this.cd) return;this.layer.style.left=-3000;this.layer.style.top=-3000;clearInterval (this.uid);awmun=0;if (this.pi!=null) if (this.pi.pm.selectedItem<1){this.pi.shst(0);}else  {if (this.pi.ind==this.pi.pm.selectedItem-1 && this.pi.ps.ind==0){this.pi.shst(2);} else  {this.pi.shst(0);}}if (this.jcoc!=null && ! this.cd) eval("setTimeout(\""+this.jcoc+"\",10);");this.cd=1;}}
function awmchdt(flg){var p;for (p=0; p<this.it.length; p++){if (this.it[p].sm!=n) this.it[p].sm.hdt(0);}if (arguments.length==1 && !this.cd) this.show(0);}
function awmmcm(){if (this.mio) return;for (var cno=(this.cll && awmctm==null)?0:1; cno<this.cn.length; cno++) this.cn[cno].show(0);if (awmSubmenusFrame!=""){for (p=0; p<this.cn[0].it.length; p++){if (this.cn[0].it[p].sm!=n) this.cn[0].it[p].sm.pm.cm();}}}
function awmodmd(){for (mno=0; mno<awmm.length; mno++){awmm[mno].cm();}}
function awmocmo(){this.mio=1;this.pm.mio=1;if (this.pi!=null) this.pi.shst((this.swn==0)?1:2);if (this.ind>0) clearTimeout(this.pi.ps.hsid);clearTimeout(this.hsid);}
function awmocmot(){this.mio=0;this.pm.mio=0;if (!this.pm.ss){var nth=setTimeout("awmm["+this.pm.ind+"].cm();",awmhd);if (awmSubmenusFrame=="") this.hsid=setTimeout("awmm["+this.pm.ind+"].cn["+this.ind+"].hdt("+((this.ind==0)?"":"0")+");",awmhd);}}
function awmiss(sts){if (sts==2 && this.pm.st==2) sts=1;for (q=0; q<this.pm.st; q++){this.layer[q].style.visibility=(q==sts)?"visible":"hidden";}}
function awmoimo(){this.shst(1);if (awmctm!=null) return;if (awmSubmenusFrame!=""){eval ("var frex=window.top."+awmSubmenusFrame);if (frex){eval("this.sm=window.top."+awmSubmenusFrame+".awm"+this.pm.nm+"_sub_"+(this.ind+1));if (this.sm){this.sm.pi=this;this.sm.pm.ss=this.pm.ss;} else  this.sm=null;}}this.ps.mio=1;this.pm.mio=1;this.ps.hdt();window.status=this.sbt;if (this.sm!=n) if (!this.sm.swn){clearTimeout(this.sm.hsid);this.sm.show(1);}if (this.jc[1]!=null) eval("setTimeout(\""+this.jc[1]+"\",10);");}
function awmoimot(){if (this.sm==null || (this.sm!=null && this.sm.cd)){if (this.pm.selectedItem<1){this.shst(0);}else  {if (this.ps.ind==0 && this.ind==this.pm.selectedItem-1){this.shst(2);} else  {this.shst(0);}}}if (!this.pm.ss){if (this.sm!=n && awmSubmenusFrame=="") this.sm.hsid=setTimeout("awmm["+this.pm.ind+"].cn["+this.sm.ind+"].hdt(0);",awmhd);}window.status=awmDefaultStatusbarText;if (this.jc[0]!=null) eval("setTimeout(\""+this.jc[0]+"\",10);");}
function awmoimd(){this.shst(2);if (this.sm!=n) if (this.sm.swn){clearTimeout(this.sm.hsid);this.sm.show(1);}if (this.jc[2]!=null) eval(this.jc[2]);if (this.url!=null){if (this.tf==null) window.location=this.url;else  if (this.tf=="new") setTimeout("window.open('"+this.url+"');",50);else  if (this.tf=="top") window.top.location=this.url;else  eval("window.top."+this.tf+".location=this.url");}}
function awmoimu(){this.pi.shst(1);}
function awmotmd(e){this.prc.pm.mio=0;awmctm=this.prc;this.prc.pm.cm();this.prc.pm.mio=1;awmmox=e.clientX-awmctm.layer.offsetLeft;awmmoy=e.clientY-awmctm.layer.offsetTop;awmml=awmctm.layer.offsetLeft-awmctm.layer.style.left;awmmt=awmctm.layer.offsetTop-awmctm.layer.style.top;}
function awmotmm(e){if (awmctm!=null){awmctm.left=awmctm.layer.style.left=e.clientX-awmmox;awmctm.top=awmctm.layer.style.top=e.clientY-awmmoy;}}
function awmotmu(e){if (awmctm!=null){awmctm.pm.rep=1;awmctm=null;}}
function awmpc(){this.fe();this.arr();var me=this.pm;var vl=window.pageXOffset,vt=window.pageYOffset,vr=vl+window.innerWidth,vb=vt+window.innerHeight;var layerHeight=parseFloat(this.layer.style.height.replace(/px/, ""))+((this.cs.bs=="none")?0:2*this.cs.bw);var layerWidth=parseFloat(this.layer.style.width.replace(/px/, ""))+((this.cs.bs=="none")?0:2*this.cs.bw);if (this.pi==null){awmlssx=awmlsx=window.pageXOffset;awmlssy=awmlsy=window.pageYOffset;if (document.getElementById("awmAnchor-"+this.pm.nm)){var tmpEl=document.getElementById("awmAnchor-"+this.pm.nm);this.left=0;this.top=0;if (me.rc==4){this.left-=layerWidth/2}if (me.rc==1 || me.rc==2){this.left-=layerWidth}if (me.rc==2 || me.rc==3){this.top-=layerHeight}this.left+=(this.pm.dft==1 || this.pm.dft==3 || this.pm.dft==4 || this.pm.dft==6)?vl:0;this.top+=(this.pm.dft==1 || this.pm.dft==2 || this.pm.dft==4 || this.pm.dft==5)?vt:0;while (tmpEl!=null){this.top+=tmpEl.offsetTop;this.left+=tmpEl.offsetLeft;tmpEl=tmpEl.parentNode;}this.left=this.layer.style.left=document.getElementById("awmAnchor-"+this.pm.nm).offsetLeft+this.pm.offX+this.left;this.top=this.layer.style.top=document.getElementById("awmAnchor-"+this.pm.nm).offsetTop+this.pm.offY+this.top;} else  {var crn=me.crn;var dof=(this.pm.dft==1 || this.pm.dft==3 || this.pm.dft==4 || this.pm.dft==6)?vl:0;this.layer.style.left=this.left=-3000;this.layer.style.top=this.top=-3000;this.layer.style.left=this.left=(crn==0 || crn==4 || crn==3)?(me.dx+dof):((crn==1 || crn==6 || crn==2)?vr-layerWidth-me.dx:vl+(vr-vl-layerWidth)/2);this.layer.style.left=this.left+=this.pm.offX;dof=(this.pm.dft==1 || this.pm.dft==2 || this.pm.dft==4 || this.pm.dft==5)?vt:0;this.layer.style.top=this.top=(crn==0 || crn==5 || crn==1)?(me.dy+dof):((crn==3 || crn==7 || crn==2)?vb-layerHeight-me.dy:vt+(vb-vt-layerHeight)/2);this.layer.style.top=this.top+=this.pm.offY;}} else  {var psl=this.pi.ps.layer;var pil=this.pi.layer[0];this.lod=this.od;if (this.lod==0){if (this.pi.ps.ct==0)this.lod=((psl.offsetLeft+psl.offsetWidth+this.swr+layerWidth>vr) && (psl.offsetLeft-this.swr-layerWidth>vl))?2:1;else this.lod=((psl.offsetTop+psl.offsetHeight+this.swr+layerHeight>vb) && (psl.offsetTop-this.swr-layerHeight>vl))?2:1;}if (this.pi.ps.ct==0){this.left=this.layer.style.left=(this.lod==1)?((this.pm.submenusFrameOffset>-9000 && this.ind==0)?window.pageXOffset:psl.offsetLeft+psl.offsetWidth)+this.swr:psl.offsetLeft-layerWidth-this.swr;if (this.pm.submenusFrameOffset>-9000 && this.ind==0 && awmRightToLeftFrame==1){this.left=this.layer.style.left=window.innerWidth-layerWidth-this.swr;}this.top=((this.sa==0)?psl.offsetTop-this.pi.ps.cs.bw+pil.offsetTop:((this.sa==1)?psl.offsetTop:((this.sa==2)?psl.offsetTop+psl.offsetHeight-layerHeight:psl.offsetTop+(psl.offsetHeight-layerHeight)/2)));this.layer.style.top=this.top+=((this.pm.submenusFrameOffset>-9000 && this.ind==0)?this.pm.submenusFrameOffset-this.pi.ps.doy+window.pageYOffset:0);} else {this.left=(this.sa==0)?((is60)?0:psl.offsetLeft+this.pi.ps.cs.bw)+pil.offsetLeft:((this.sa==1)?psl.offsetLeft:((this.sa==2)?psl.offsetLeft+psl.offsetWidth-layerWidth:psl.offsetLeft+(psl.offsetWidth-layerWidth)/2));this.layer.style.left=this.left+=((this.pm.submenusFrameOffset>-9000 && this.ind==0)?this.pm.submenusFrameOffset-this.pi.ps.dox+window.pageXOffset:0);if (this.left+layerWidth>vr) this.layer.style.left=this.left=vr-layerWidth;this.top=this.layer.style.top=(this.lod==1)?((this.pm.submenusFrameOffset>-9000 && this.ind==0)?window.pageYOffset:psl.offsetTop+psl.offsetHeight)+this.swr:psl.offsetTop-layerHeight-this.swr;}}}
function awmu(){if (awmuc>10 || awmctu.shw==2){awmctu.layer.style.visibility="hidden";awmctu.layer.style.visibility="visible";}if (awmuc>10){clearInterval (awmctu.uid);awmun=0;return;}var layer=awmctu.layer;layerWidth=layer.offsetWidth;layerHeight=layer.offsetHeight;bWidth=-((awmctu.cs.bs!="none")?awmctu.cs.bw:0);switch (awmud){case 1: if (awmctu.shw==1){layer.style.left=awmctu.left-layerWidth*(10-awmuc)/10;layer.style.clip="rect("+bWidth+","+layerWidth+","+layerHeight+","+(bWidth+Math.round(layerWidth*(10-awmuc)/10))+")";} else  layer.style.clip="rect("+bWidth+","+Math.round(layerWidth*awmuc/10)+","+layerHeight+","+bWidth+")";break;case 2: if (awmctu.shw==1){layer.style.left=awmctu.left+layerWidth*(10-awmuc)/10;layer.style.clip="rect("+bWidth+","+Math.round(layerWidth*awmuc/10)+","+layerHeight+","+bWidth+")";} else  layer.style.clip="rect("+bWidth+","+layerWidth+","+layerHeight+","+(bWidth+layerWidth*(10-awmuc)/10)+")";break;case 3: if (awmctu.shw==1){layer.style.top=awmctu.top-layerHeight*(10-awmuc)/10;layer.style.clip="rect("+(bWidth+Math.round(layerHeight*(10-awmuc)/10))+","+layerWidth+","+layerHeight+","+bWidth+")";} else  layer.style.clip="rect("+bWidth+","+layerWidth+","+Math.round(layerHeight*awmuc/10)+","+bWidth+")";break;case 4: if (awmctu.shw==1){layer.style.top=awmctu.top+layerHeight*(10-awmuc)/10;layer.style.clip="rect("+bWidth+","+layerWidth+","+Math.round(layerHeight*awmuc/10)+","+bWidth+")";} else  layer.style.clip="rect("+(bWidth+Math.round(layerHeight*(10-awmuc)/10))+","+layerWidth+","+layerHeight+","+bWidth+")";break;case 5: if (awmctu.shw==1){layer.style.left=awmctu.left-layerWidth*(10-awmuc)/10;layer.style.top=awmctu.top-layerHeight*(10-awmuc)/10;layer.style.clip="rect("+(bWidth+Math.round(layerHeight*(10-awmuc)/10))+","+layerWidth+","+layerHeight+","+bWidth+Math.round(layerWidth*(10-awmuc)/10)+")";} else  layer.style.clip="rect("+bWidth+","+Math.round(layerWidth*awmuc/10)+","+Math.round(layerHeight*awmuc/10)+","+bWidth+")";break;case 6: if (awmctu.shw==1){layer.style.left=awmctu.left-layerWidth*(10-awmuc)/10;layer.style.top=awmctu.top+layerHeight*(10-awmuc)/10;layer.style.clip="rect("+bWidth+","+layerWidth+","+Math.round(layerHeight*awmuc/10)+","+(bWidth+Math.round(layerWidth*(10-awmuc)/10))+")";} else  layer.style.clip="rect("+(bWidth+Math.round(layerHeight*(10-awmuc)/10))+","+Math.round(layerWidth*awmuc/10)+","+layerHeight+","+bWidth+")";break;case 7: if (awmctu.shw==1){layer.style.left=awmctu.left+layerWidth*(10-awmuc)/10;layer.style.top=awmctu.top-layerHeight*(10-awmuc)/10;layer.style.clip="rect("+(bWidth+Math.round(layerHeight*(10-awmuc)/10))+","+Math.round(layerWidth*awmuc/10)+","+layerHeight+","+(bWidth+layerWidth*(10-awmuc)/10)+")";} else  layer.style.clip="rect("+bWidth+","+layerWidth+","+Math.round(layerHeight*awmuc/10)+","+(bWidth+layerWidth*(10-awmuc)/10)+")";break;case 8: if (awmctu.shw==1){layer.style.left=awmctu.left+layerWidth*(10-awmuc)/10;layer.style.top=awmctu.top+layerHeight*(10-awmuc)/10;layer.style.clip="rect("+bWidth+","+Math.round(layerWidth*awmuc/10)+","+Math.round(layerHeight*awmuc/10)+","+bWidth+")";} else  layer.style.clip="rect("+(bWidth+Math.round(layerHeight*(10-awmuc)/10))+","+layerWidth+","+layerHeight+","+(bWidth+layerWidth*(10-awmuc)/10)+")";break;}awmuc+=2;}
function awmcu(){clearInterval(this.uid);this.layer.style.clip="rect(-100,-100,-100,-100)";this.layer.style.visibility="visible";awmun=1;awmuc=0;awmud=(this.ud!=0)?this.ud:(this.lod+((this.pi.ps.ct==0)?0:2));awmctu=this;this.uid=setInterval("awmu()",50);}
function awmwr(){if (!(awmSubmenusFrameOffset>-9000)){for (var mno=0; mno<awmm.length; mno++){if (awmm[mno].cn[0].ct==2) awmm[mno].cn[0].layer.style.width=window.innerWidth-2*awmm[mno].cn[0].cs.bw;if (!awmm[mno].rep && !awmm[mno].cll) awmm[mno].cn[0].pc();if (awmm[mno].cll && !awmm[mno].cd) awmm[mno].cn[0].hdt(0);}}}
function awmwu(){if (awmSubmenusFrameOffset>-9000){for (var mno=0; mno<awmm.length; mno++){if (awmm[mno].cn[0].pi!=null){awmm[mno].cn[0].pi.shst(0);awmm[mno].cn[0].pi.sm=null;}}}}
function awmwl(){}
function awmd(){var csx=window.pageXOffset;var csy=window.pageYOffset;var clientX=window.innerWidth;var clientY=window.innerHeight;var sx=10;var sy=10;var divider=5;var snx,sny;if (csx!=awmlsx ||csy!=awmlsy){for (var mno=0; mno<awmm.length; mno++){var crm=awmm[mno];crm.mio=0;crm.cm();if (crm.dft==1 || crm.dft==3){crm.cn[0].left=crm.cn[0].layer.style.left=parseInt(crm.cn[0].layer.style.left)+csx-awmlsx;if (awmSubmenusFrame!='' && crm.cn[0].ct>0) crm.cn[0].dox=csx;}if (crm.dft==1 || crm.dft==2){crm.cn[0].top=crm.cn[0].layer.style.top=parseInt(crm.cn[0].layer.style.top)+csy-awmlsy;if (awmSubmenusFrame!='' && crm.cn[0].ct==0) crm.cn[0].doy=csy;}}awmlsx=csx;awmlsy=csy;}if (csx!=awmlssx || csy!=awmlssy){for (var mno=0; mno<awmm.length; mno++){var crm=awmm[mno];if (crm.cn[0].ft){if ((crm.dft==4 || crm.dft==6) && csx!=awmlssx){crm.mio=0;crm.cm();snx=Math.abs(csx-awmlssx)/(csx-awmlssx);if((Math.round(Math.abs(csx-awmlssx)/divider))>=sx) sx=Math.round(Math.abs(csx-awmlssx)/divider);if (Math.abs(csx-awmlssx)<sx) sx=Math.abs(csx-awmlssx);if (crm.cn[0].left<csx || crm.cn[0].left>csx+clientX){sx=Math.abs(csx-crm.cn[0].left);if (snx==-1) sx-=clientX;}crm.cn[0].left=crm.cn[0].layer.style.left=parseInt(crm.cn[0].layer.style.left)+snx*sx;if (awmSubmenusFrame!='' && crm.cn[0].ct>0) crm.cn[0].dox=csx;}if ((crm.dft==4 || crm.dft==5) && csy!=awmlssy){crm.mio=0;crm.cm();sny=Math.abs(csy-awmlssy)/(csy-awmlssy);if((Math.round(Math.abs(csy-awmlssy)/divider))>=sy) sy=Math.round(Math.abs(csy-awmlssy)/divider);if (Math.abs(csy-awmlssy)<sy) sy=Math.abs(csy-awmlssy);if (crm.cn[0].top<csy || crm.cn[0].top>csy+clientY){sy=Math.abs(csy-crm.cn[0].top);if (sny==-1) sy-=clientY;}crm.cn[0].top=crm.cn[0].layer.style.top=parseInt(crm.cn[0].layer.style.top)+sny*sy;if (awmSubmenusFrame!='' && crm.cn[0].ct==0) crm.cn[0].doy=csy;}}}if (csx!=awmlssx) awmlssx+=snx*sx;if (csy!=awmlssy) awmlssy+=sny*sy;}}
function awmdb(mi){var crc=awmm[mi].cn[0];if (document.getElementById(crc.id).offsetWidth>0 || !is60){if (!awmm[mi].cll) crc.show(1);} else  setTimeout("awmdb("+mi+")",0);}
function awmbmm(){if (typeof(awmTarget)!='undefined' && this.ind>0) return;document.onmousedown=awmodmd;window.status="."+(this.ind+1);this.ght();this.whtd();awmdb(this.ind);window.status=awmDefaultStatusbarText;awmdid=setInterval("awmd()",75);awmsoo=awmso+1;}
function awmShowMenu (menuName,x,y,frame){var ml;if (arguments.length<4 || frame==null) ml=awmm;else  {eval ("var frex=window.top."+awmSubmenusFrame);if (!frex) return;eval("ml=window.top."+frame+".awmm;");}if (ml){var i=0;while (ml[i].nm!=menuName && i<ml.length-1) i++;if (ml[i].nm==menuName){if (arguments.length<3 || x==null || y==null) ml[i].cn[0].show(1);else  ml[i].cn[0].show(1,x,y);}ml=null;}}