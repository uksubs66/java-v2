/**

 */

(function() {
    tinymce.create('tinymce.plugins.SplitDiv', {
        init : function(ed, url) {
            var t = this;
            t.editor = ed;
                        
            tinymce.DOM.loadCSS(url + '/css/content.css');

            ed.addCommand('mceAddSlideElementAfter', function() {
                            
                var sn = ed.selection.getNode();
                var osn = sn;
                if(!ed.dom.hasClass(sn.parentNode,'slide_container')){
                    sn = ed.dom.getParent(sn,function(node){
                        return ed.dom.hasClass(node.parentNode,'slide_container') && node.nodeName == 'DIV';
                    });
                }
                if(sn == null){
                    return;
                }
                            
                var cn = sn.cloneNode(false);
                cn.removeAttribute('id');
                var pn = sn.parentNode;
                pn.insertBefore(cn,sn.nextSibling);
                ed.nodeChanged();
            });
            
            ed.addCommand('mceRemoveSlideElement', function() {
                            
                var sn = ed.selection.getNode();
                var osn = sn;
                if(!ed.dom.hasClass(sn.parentNode,'slide_container')){
                    sn = ed.dom.getParent(sn,function(node){
                        return ed.dom.hasClass(node.parentNode,'slide_container') && node.nodeName == 'DIV';
                    });
                }
                if(sn == null){
                    return;
                }
                
                var prbn = ed.dom.getPrev(sn, 'DIV');
                if(prbn == null || !ed.dom.hasClass(prbn.parentNode,'slide_container') || ed.dom.hasClass(prbn,'clearfloat') || prbn.nodeName != 'DIV')
                    prbn = null;
                var nxbn = ed.dom.getNext(sn, 'DIV');
                if(nxbn == null || !ed.dom.hasClass(nxbn.parentNode,'slide_container') || ed.dom.hasClass(nxbn,'clearfloat') || nxbn.nodeName != 'DIV')
                    nxbn = null;
                if(nxbn || prbn){
                    var pn = sn.parentNode;
                    pn.removeChild(sn);
                    if(nxbn){
                        ed.selection.select(nxbn);
                    } else {
                        ed.selection.select(prbn);
                    }
                    ed.nodeChanged();
                } 
            });
            
            ed.addCommand('mceMoveSlideElementUp', function() {
                            
                var sn = ed.selection.getNode();
                var osn = sn;
                if(!ed.dom.hasClass(sn.parentNode,'slide_container')){
                    sn = ed.dom.getParent(sn,function(node){
                        return ed.dom.hasClass(node.parentNode,'slide_container') && node.nodeName == 'DIV';
                    });
                }
                if(sn == null){
                    return;
                }
                
                var prbn = ed.dom.getPrev(sn, 'DIV');
                if(prbn == null || !ed.dom.hasClass(prbn.parentNode,'slide_container') || ed.dom.hasClass(prbn,'clearfloat') || prbn.nodeName != 'DIV')
                    prbn = null;
                
                if(prbn){
                    var pn = prbn.parentNode;
                    pn.insertBefore(sn,prbn);
                    ed.selection.select(osn);
                    ed.nodeChanged();
                }

            });
            
            ed.addCommand('mceMoveSlideElementDown', function() {
                            
                var sn = ed.selection.getNode();
                var osn = sn;
                if(!ed.dom.hasClass(sn.parentNode,'slide_container')){
                    sn = ed.dom.getParent(sn,function(node){
                        return ed.dom.hasClass(node.parentNode,'slide_container') && node.nodeName == 'DIV';
                    });
                }
                if(sn == null || !ed.dom.hasClass(sn.parentNode,'slide_container')){
                    return;
                }
                
                var nxbn = ed.dom.getNext(sn, 'DIV');
                if(nxbn == null || !ed.dom.hasClass(nxbn.parentNode,'slide_container') || ed.dom.hasClass(nxbn,'clearfloat') || nxbn.nodeName != 'DIV')
                    nxbn = null;
                
                if(nxbn){
                    var pn = nxbn.parentNode;
                    pn.insertBefore(sn,nxbn.nextSibling);
                    ed.selection.select(osn);
                    ed.nodeChanged();
                } 

            });
            
            ed.addCommand('mceAddBlockContainerAfter', function() {
                            
                var sn = ed.selection.getNode();
                var osn = sn;
                if(!ed.dom.hasClass(sn,'block_container')){
                    sn = ed.dom.getParent(sn,'DIV.block_container');
                }
                if(sn == null || !ed.dom.hasClass(sn,'block_container')){
                    return;
                }
                            
                var cn = sn.cloneNode(false);
                cn.removeAttribute('id');
                var pn = sn.parentNode;
                pn.insertBefore(cn,sn.nextSibling);
                ed.nodeChanged();
            });
            
            ed.addCommand('mceRemoveBlockContainer', function() {
                            
                var sn = ed.selection.getNode();
                var osn = sn;
                if(!ed.dom.hasClass(sn,'block_container')){
                    sn = ed.dom.getParent(sn,'DIV.block_container');
                }
                if(sn == null || !ed.dom.hasClass(sn,'block_container')){
                    return;
                }

                var prbn = ed.dom.getPrev(sn, 'DIV.block_container');
                var nxbn = ed.dom.getNext(sn, 'DIV.block_container');
                if(nxbn || prbn){
                    var pn = sn.parentNode;
                    if(nxbn){
                        ed.selection.select(nxbn);
                    } else {
                        ed.selection.select(prbn);
                    }
                    pn.removeChild(sn);
                    ed.nodeChanged();
                } 
                
                
            });
            
            ed.addCommand('mceAddBlockContainerBefore', function() {
                            
                var sn = ed.selection.getNode();
                var osn = sn;
                if(!ed.dom.hasClass(sn,'block_container')){
                    sn = ed.dom.getParent(sn,'DIV.block_container');
                }
                if(sn == null || !ed.dom.hasClass(sn,'block_container')){
                    return;
                }
                            
                var cn = sn.cloneNode(false);
                cn.removeAttribute('id');
                var pn = sn.parentNode;
                pn.insertBefore(cn,sn);
                ed.nodeChanged();
            });
            
            ed.addCommand('mceMoveBlockContainerUp', function() {
                            
                var sn = ed.selection.getNode();
                var osn = sn;
                if(!ed.dom.hasClass(sn,'block_container')){
                    sn = ed.dom.getParent(sn,'DIV.block_container');
                }
                //var rn = ed.dom.getRoot();
                if(sn == null || !ed.dom.hasClass(sn,'block_container')){
                    return;
                }
                var prbn = ed.dom.getPrev(sn, 'DIV.block_container');
                if(prbn){
                    var pn = prbn.parentNode;
                    pn.insertBefore(sn,prbn);
                    ed.selection.select(osn);
                    ed.nodeChanged();
                }
                
            });
            
            ed.addCommand('mceMoveBlockContainerDown', function() {
                            
                var sn = ed.selection.getNode();
                var osn = sn;
                if(!ed.dom.hasClass(sn,'block_container')){
                    sn = ed.dom.getParent(sn,'DIV.block_container');
                }
                //var rn = ed.dom.getRoot();
                if(sn == null || !ed.dom.hasClass(sn,'block_container')){
                    return;
                }
                var nxbn = ed.dom.getNext(sn, 'DIV.block_container');
                if(nxbn){
                    var pn = nxbn.parentNode;
                    pn.insertBefore(sn,nxbn.nextSibling);
                    ed.selection.select(osn);
                    ed.nodeChanged();
                } 
                
            });
            
            ed.addCommand('mceAddSlideContainer', function() {
                            
                var sn = ed.selection.getNode();
                var osn = sn;
                var s_block = 'DIV,TD,TH,LI,BLOCKQUOTE';
                if(ed.dom.getParent(sn, 'DIV.slide_container') || ed.dom.hasClass(sn,'slide_container') || (s_block.indexOf(sn.nodeName) < 0)){
                    sn = ed.dom.getParent(sn,function(node){
                        return !(ed.dom.getParent(node, 'DIV.slide_container')) && !ed.dom.hasClass(node,'slide_container') && (s_block.indexOf(node.nodeName) > -1);
                    });
                }
                if(sn == null){
                    return;
                }
                            
                var nn = ed.dom.create('div',{
                    'class':'slide_container'
                },'<span class="slide_settings">transition-effect:fade,transition-speed:400,display-length:6000</span><div></div><div></div><div></div><div class="clearfloat"></div>');
                var rn = sn.firstChild;
                while (rn != null && rn.nodeType != 1){
                    rn = rn.nextSibling;
                }
                sn.appendChild(nn);
                if(rn && rn.nodeName == "BR"){
                    sn.removeChild(rn);
                }
                ed.nodeChanged();
            });
            
            ed.addCommand('mceRemoveSlideContainer', function() {
                            
                var sn = ed.selection.getNode();
                var osn = sn;
                if(!ed.dom.hasClass(sn,'slide_container')){
                    sn = ed.dom.getParent(sn,'DIV.slide_container');
                }
                if(sn == null){
                    return;
                }
                var pn = sn.parentNode;
                pn.removeChild(sn);
                ed.nodeChanged();
                  
            });
            
            
            ed.addCommand('mceMoveSlideContainerUp', function() {
                            
                var sn = ed.selection.getNode();
                var osn = sn;
                if(!ed.dom.hasClass(sn,'slide_container')){
                    sn = ed.dom.getParent(sn,'DIV.slide_container');
                }
                //var rn = ed.dom.getRoot();
                if(sn == null){
                    return;
                }
                var prbn = ed.dom.getPrev(sn, function(node){
                    return !ed.dom.hasClass(node,'clearfloat');
                });
                if(prbn){
                    var pn = prbn.parentNode;
                    pn.insertBefore(sn,prbn);
                    ed.selection.select(osn);
                    ed.nodeChanged();
                }
                
            });
            
            ed.addCommand('mceMoveSlideContainerDown', function() {
                            
                var sn = ed.selection.getNode();
                var osn = sn;
                if(!ed.dom.hasClass(sn,'slide_container')){
                    sn = ed.dom.getParent(sn,'DIV.slide_container');
                }
                //var rn = ed.dom.getRoot();
                if(sn == null){
                    return;
                }
                var nxbn = ed.dom.getNext(sn, function(node){
                    return !ed.dom.hasClass(node,'clearfloat');
                });
                if(nxbn){
                    var pn = nxbn.parentNode;
                    pn.insertBefore(sn,nxbn.nextSibling);
                    ed.selection.select(osn);
                    ed.nodeChanged();
                } 
                
            });
            
            ed.addCommand('mceAddTextBlock', function() {
                            
                var sn = ed.selection.getNode();
                var osn = sn;
                var s_block = 'DIV,TD,TH,LI,BLOCKQUOTE';
                if((s_block.indexOf(sn.nodeName) < 0)){
                    sn = ed.dom.getParent(sn,function(node){
                        return (s_block.indexOf(node.nodeName) > -1);
                    });
                }
                if(sn == null){
                    return;
                }
                            
                var nn = ed.dom.create('p',{},'New Text Block');
                var rn = sn.firstChild;
                while (rn != null && rn.nodeType != 1){
                    rn = rn.nextSibling;
                }
                sn.appendChild(nn);
                if(rn && rn.nodeName == "BR"){
                    sn.removeChild(rn);
                    ed.selection.select(nn);
                }
                ed.nodeChanged();
            });
            
            ed.addCommand('mceRemoveTextBlock', function() {
                            
                var sn = ed.selection.getNode();
                var osn = sn;
                var t_block = 'PRE,CODE,P,H1,H2,H3,H4,H5,H6,ADDRESS,BLOCKQUOTE';
                if((t_block.indexOf(sn.nodeName) < 0)){
                    sn = ed.dom.getParent(sn,function(node){
                        return (t_block.indexOf(node.nodeName) > -1);
                    });
                }
                if(sn == null){
                    return;
                }
                var pn = sn.parentNode;
                pn.removeChild(sn);
                ed.nodeChanged();
                  
            });
            
        
            ed.onNodeChange.add(function(ed, cm, n) {
                // Activate / Disable the link buttons
                var sbn = n;
                var ssn = n;
                var sscn = n;
                var stn = n;
                
                if(n.nodeName == 'DIV' && (n.style.position != 'absolute' && n.style.position != 'relative')){
                    n.onresizestart = function(){
                        this.resize_style = this.getAttribute('style');
                    }
                    n.onresizeend = function(){
                        this.setAttribute('style',this.resize_style);
                        
                    }
                }
                
                // Check if we are using any button
                var c = cm.get('add_block_container_after');
                var c2 = cm.get('add_block_container_before');
                var c3 = cm.get('move_block_container_up');
                var c4 = cm.get('move_block_container_down');
                
                var c5 = cm.get('add_slide_element_after');
                var c6 = cm.get('move_slide_element_up');
                var c7 = cm.get('move_slide_element_down');
                
                var c8 = cm.get('remove_block_container');
                var c9 = cm.get('remove_slide_element');
                
                var c10 = cm.get('add_slide_container');
                var c11 = cm.get('remove_slide_container');
                var c12 = cm.get('move_slide_container_up');
                var c13 = cm.get('move_slide_container_down');
                
                var c14 = cm.get('add_text_block');
                var c15 = cm.get('remove_text_block');
                
                // Check if we are a text block
                var t_block = 'PRE,CODE,P,H1,H2,H3,H4,H5,H6,ADDRESS,BLOCKQUOTE';
                if((t_block.indexOf(stn.nodeName) < 0)){
                    stn = ed.dom.getParent(stn,function(node){
                        return (t_block.indexOf(node.nodeName) > -1);
                    });
                }
                
                // Check if we have a block container parent
                if(!ed.dom.hasClass(sbn,'block_container')){
                    sbn = ed.dom.getParent(sbn,'DIV.block_container');
                }
                // Disable resize in IE
                if(sbn){
                    sbn.onresizestart = function(){
                        this.resize_style = this.getAttribute('style');
                    }
                    sbn.onresizeend = function(){
                        this.setAttribute('style',this.resize_style);
                        
                    }
                }
                
                // Check if we have a slide parent
                if(!ed.dom.hasClass(ssn.parentNode,'slide_container')){
                    ssn = ed.dom.getParent(ssn,function(node){
                        return ed.dom.hasClass(node.parentNode,'slide_container') && node.nodeName == 'DIV';
                    });
                }
                // Disable resize in IE
                if(ssn){
                    ssn.onresizestart = function(){
                        this.resize_style = this.getAttribute('style');
                    }
                    ssn.onresizeend = function(){
                        this.setAttribute('style',this.resize_style);
                        
                    }
                }
                
                // Check if we have a slide container parent
                if(!ed.dom.hasClass(sscn,'slide_container')){
                    sscn = ed.dom.getParent(sscn,'DIV.slide_container');
                }
                
                // Check if we should displabe buttons from other plugins
                var op1 = cm.get('formatselect');
                if(op1){
                    if(n.nodeName == "DIV" || ed.dom.hasClass(n,'slide_settings') || (n.nodeName == "BR" && n.parentNode.nodeName == "DIV")){
                        op1.setActive(false);
                        op1.setDisabled(true);
                    } else {
                        op1.setDisabled(false);
                    }
                }
                
                
                // Check for siblings of block container
                var prbn = null;
                var nxbn = null;
                if(sbn != null && ed.dom.hasClass(sbn,'block_container')){
                    prbn = ed.dom.getPrev(sbn, 'DIV.block_container');
                    if(!ed.dom.hasClass(sbn.previousSibling,'block_container'))
                        prbn = null;
                    nxbn = ed.dom.getNext(sbn, 'DIV.block_container');
                    if(!ed.dom.hasClass(sbn.nextSibling,'block_container'))
                        nxbn = null;
                } else {
                    sbn = null;
                }
                
                // Check for siblings of slide
                var prsn = null;
                var nxsn = null;
                if(ssn != null && ed.dom.hasClass(ssn.parentNode,'slide_container') && ssn.nodeName == 'DIV'){
                    prsn = ed.dom.getPrev(ssn, 'DIV');
                    if(prsn == null || !ed.dom.hasClass(prsn.parentNode,'slide_container') || ed.dom.hasClass(prsn,'clearfloat') || prsn.nodeName != 'DIV')
                        prsn = null;
                    nxsn = ed.dom.getNext(ssn, 'DIV');
                    if(nxsn == null || !ed.dom.hasClass(nxsn.parentNode,'slide_container') || ed.dom.hasClass(nxsn,'clearfloat') || nxsn.nodeName != 'DIV')
                        nxsn = null;
                } else {
                    ssn = null; 
                }
                
                // Check for siblings of slide container
                var prscn = null;
                var nxscn = null;
                if(sscn != null){
                    prscn = ed.dom.getPrev(sscn, function(node){
                        return !ed.dom.hasClass(node,'clearfloat');
                    });
                    nxscn = ed.dom.getNext(sscn, function(node){
                        return !ed.dom.hasClass(node,'clearfloat');
                    });
                }
                
                
                // Set state of any buttons
                if(c){
                    if (sbn) {
                        c.setDisabled(false);
                    } else {
                        c.setActive(false);
                        c.setDisabled(true);
                    }
                }
                if(c2){
                    if (sbn) {
                        c2.setDisabled(false);
                    } else {
                        c2.setActive(false);
                        c2.setDisabled(true);
                    }
                }
                if(c3){
                    if (prbn) {
                        c3.setDisabled(false);
                    } else {
                        c3.setActive(false);
                        c3.setDisabled(true);
                    }
                }
                if(c4){
                    if (nxbn) {
                        c4.setDisabled(false);
                    } else {
                        c4.setActive(false);
                        c4.setDisabled(true);
                    }
                }
                
                if(c5){
                    if (ssn != null) {
                        c5.setDisabled(false);
                    } else {
                        c5.setActive(false);
                        c5.setDisabled(true);
                    }
                }
                
                if(c6){
                    if (prsn != null) {
                        c6.setDisabled(false);
                    } else {
                        c6.setActive(false);
                        c6.setDisabled(true);
                    }
                }
                
                if(c7){
                    if (nxsn != null) {
                        c7.setDisabled(false);
                    } else {
                        c7.setActive(false);
                        c7.setDisabled(true);
                    }
                }
                
                if(c8){
                    if (sbn != null && (prbn || nxbn)) {
                        c8.setDisabled(false);
                    } else {
                        c8.setActive(false);
                        c8.setDisabled(true);
                    }
                }
                
                if(c9){
                    if (ssn != null && (prsn || nxsn)) {
                        c9.setDisabled(false);
                    } else {
                        c9.setActive(false);
                        c9.setDisabled(true);
                    }
                }
                
                // Activate add slide container button if we are in a block container
                if(c10){
                    if (sbn != null) {
                        c10.setDisabled(false);
                    } else {
                        c10.setActive(false);
                        c10.setDisabled(true);
                    }
                }
                
                // Activate remove slide container button if we are in a slide continer
                if(c11){
                    if (sscn != null) {
                        c11.setDisabled(false);
                    } else {
                        c11.setActive(false);
                        c11.setDisabled(true);
                    }
                }
                // Activate move slide container up button if we have a previous sibling
                if(c12){
                    if (prscn != null) {
                        c12.setDisabled(false);
                    } else {
                        c12.setActive(false);
                        c12.setDisabled(true);
                    }
                }
                // Activate move slide container down button if we have a next sibling
                if(c13){
                    if (nxscn != null) {
                        c13.setDisabled(false);
                    } else {
                        c13.setActive(false);
                        c13.setDisabled(true);
                    }
                }
                
                if(c14){
                    if (sbn || ssn ||sscn || stn) {
                        c14.setDisabled(false);
                    } else {
                        c14.setActive(false);
                        c14.setDisabled(true);
                    }
                }
                
                if(c15){
                    if (stn) {
                        c15.setDisabled(false);
                    } else {
                        c15.setActive(false);
                        c15.setDisabled(true);
                    }
                }
                            
            });
            
            ed.addButton('add_text_block', {
                title : 'Add New Formattable Text Block (Paragraph by default)', 
                cmd : 'mceAddTextBlock'
            });
            ed.addButton('remove_text_block', {
                title : 'Remove Selected Text Block (Headings, Paragraphs, etc.)', 
                cmd : 'mceRemoveTextBlock'
            });
            
            ed.addButton('add_slide_container', {
                title : 'Add New Empty Slide Container', 
                cmd : 'mceAddSlideContainer'
            });
            ed.addButton('remove_slide_container', {
                title : 'Delete Selected Slide Container and its Slides', 
                cmd : 'mceRemoveSlideContainer'
            });
            
            ed.addButton('move_slide_container_up', {
                title : 'Move Slide Containter Up', 
                cmd : 'mceMoveSlideContainerUp'
            });
            ed.addButton('move_slide_container_down', {
                title : 'Move Slide Containter Down', 
                cmd : 'mceMoveSlideContainerDown'
            });
                        
            ed.addButton('add_block_container_after', {
                title : 'Add New Block Containter After', 
                cmd : 'mceAddBlockContainerAfter'
            });
            ed.addButton('add_block_container_before', {
                title : 'Add New Block Containter Before', 
                cmd : 'mceAddBlockContainerBefore'
            });
            ed.addButton('move_block_container_up', {
                title : 'Move Block Containter Up', 
                cmd : 'mceMoveBlockContainerUp'
            });
            ed.addButton('move_block_container_down', {
                title : 'Move Block Containter Down', 
                cmd : 'mceMoveBlockContainerDown'
            });
            ed.addButton('add_slide_element_after', {
                title : 'Add New Empty Slide', 
                cmd : 'mceAddSlideElementAfter'
            });
            ed.addButton('move_slide_element_up', {
                title : 'Move Slide Up', 
                cmd : 'mceMoveSlideElementUp'
            });
            ed.addButton('move_slide_element_down', {
                title : 'Move Slide Down', 
                cmd : 'mceMoveSlideElementDown'
            });
            ed.addButton('remove_block_container', {
                title : 'Delete Selected Block Containter and its Contents', 
                cmd : 'mceRemoveBlockContainer'
            });
            ed.addButton('remove_slide_element', {
                title : 'Delete Selected Slide and its Contents', 
                cmd : 'mceRemoveSlideElement'
            });
            
        },

        getInfo : function() {
            return {
                longname : 'Split Div',
                author : 'Webifi, Inc.',
                authorurl : 'http://webifi.com',
                infourl : '',
                version : '1.0'
            };
        },

        // Private methods

        _getBlockContainer : function(ed, n) {
            if(!ed.dom.hasClass(n,'block_container')){
                n = ed.dom.getParent(n,'DIV.block_container');
            }
            return n;
        }
    });

    // Register plugin
    tinymce.PluginManager.add('splitdiv', tinymce.plugins.SplitDiv);
})();