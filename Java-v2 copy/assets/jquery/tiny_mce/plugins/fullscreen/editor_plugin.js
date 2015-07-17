/**
 * editor_plugin_src.js
 *
 * Copyright 2009, Moxiecode Systems AB
 * Released under LGPL License.
 *
 * License: http://tinymce.moxiecode.com/license
 * Contributing: http://tinymce.moxiecode.com/contributing
 */

(function() {
	var DOM = tinymce.DOM;

	tinymce.create('tinymce.plugins.FullScreenPlugin', {
		init : function(ed, url) {
			var t = this, s = {}, vp, posCss;

			t.editor = ed;

			// Register commands
			ed.addCommand('mceFullScreen', function() {
				var win, de = DOM.doc.documentElement;

				if (ed.getParam('fullscreen_is_enabled')) {
					if (ed.getParam('fullscreen_new_window'))
						closeFullscreen(); // Call to close in new window
					else {
						DOM.win.setTimeout(function() {
							tinymce.dom.Event.remove(DOM.win, 'resize', t.resizeFunc);
							tinyMCE.get(ed.getParam('fullscreen_editor_id')).setContent(ed.getContent());
							tinyMCE.remove(ed);
							DOM.remove('mce_fullscreen_container');
							de.style.overflow = ed.getParam('fullscreen_html_overflow');
							DOM.setStyle(DOM.doc.body, 'overflow', ed.getParam('fullscreen_overflow'));
							DOM.win.scrollTo(ed.getParam('fullscreen_scrollx'), ed.getParam('fullscreen_scrolly'));
							tinyMCE.settings = tinyMCE.oldSettings; // Restore old settings
						}, 10);
					}

					return;
				}

				if (ed.getParam('fullscreen_new_window')) {
					win = DOM.win.open(url + "/fullscreen.htm", "mceFullScreenPopup", "fullscreen=yes,menubar=no,toolbar=no,scrollbars=no,resizable=yes,left=0,top=0,width=" + screen.availWidth + ",height=" + screen.availHeight);
					try {
						win.resizeTo(screen.availWidth, screen.availHeight);
					} catch (e) {
						// Ignore
					}
				} else {
					tinyMCE.oldSettings = tinyMCE.settings; // Store old settings
					s.fullscreen_overflow = DOM.getStyle(DOM.doc.body, 'overflow', 1) || 'auto';
					s.fullscreen_html_overflow = DOM.getStyle(de, 'overflow', 1);
					vp = DOM.getViewPort();
					s.fullscreen_scrollx = vp.x;
					s.fullscreen_scrolly = vp.y;

					// Fixes an Opera bug where the scrollbars doesn't reappear
					if (tinymce.isOpera && s.fullscreen_overflow == 'visible')
						s.fullscreen_overflow = 'auto';

					// Fixes an IE bug where horizontal scrollbars would appear
					if (tinymce.isIE && s.fullscreen_overflow == 'scroll')
						s.fullscreen_overflow = 'auto';

					// Fixes an IE bug where the scrollbars doesn't reappear
					if (tinymce.isIE && (s.fullscreen_html_overflow == 'visible' || s.fullscreen_html_overflow == 'scroll'))
						s.fullscreen_html_overflow = 'auto'; 

					if (s.fullscreen_overflow == '0px')
						s.fullscreen_overflow = '';

					DOM.setStyle(DOM.doc.body, 'overflow', 'hidden');
					de.style.overflow = 'hidden'; //Fix for IE6/7
                                        DOM.win.scrollTo(0, 0);
					vp = DOM.getViewPort();

					//if (tinymce.isIE)
					//	vp.h -= 1;

					// Use fixed position if it exists
					if (tinymce.isIE6)
						posCss = 'absolute;top:' + vp.y;
					else
						posCss = 'absolute;top:0';

					n = DOM.add(DOM.doc.body, 'div', {
						id : 'mce_fullscreen_container', 
						style : 'position:' + posCss + ';left:0;width:' + vp.w + 'px;height:' + vp.h + 'px;z-index:200000;'
                                            });
					DOM.add(n, 'div', {id : 'mce_fullscreen'});

					tinymce.each(ed.settings, function(v, n) {
						s[n] = v;
					});

					s.id = 'mce_fullscreen';
					s.width = vp.w;
					s.height = vp.h - 10;
                                        //console.log("container_load:"+JSON.stringify(vp));
					s.fullscreen_is_enabled = true;
					s.fullscreen_editor_id = ed.id;
					s.theme_advanced_resizing = false;
					s.save_onsavecallback = function() {
						ed.setContent(tinyMCE.get(s.id).getContent());
						ed.execCommand('mceSave');
					};

					tinymce.each(ed.getParam('fullscreen_settings'), function(v, k) {
						s[k] = v;
					});

					if (s.theme_advanced_toolbar_location === 'external')
						s.theme_advanced_toolbar_location = 'top';

					t.fullscreenEditor = new tinymce.Editor('mce_fullscreen', s);

					t.resizeFunc = function() {
						var vp = tinymce.DOM.getViewPort(), fed = t.fullscreenEditor, outerSize, innerSize;
						// Get outer/inner size to get a delta size that can be used to calc the new iframe size
						outerSize = tinymce.DOM.getSize("mce_fullscreen_tbl");
						innerSize = fed.dom.getSize(fed.getContainer().getElementsByTagName('iframe')[0]);
                                                //console.log("container_resize:"+JSON.stringify(vp));
						//fed.theme.resizeTo(vp.w - outerSize.w + innerSize.w, vp.h - outerSize.h + innerSize.h);
                                                fed.theme.resizeTo(vp.w - (outerSize.w - innerSize.w), vp.h - (outerSize.h - innerSize.h));
                                                
					};
                                        
                                        t.fullscreenEditor.onInit.add(function() {
						t.fullscreenEditor.setContent(ed.getContent());
						t.fullscreenEditor.focus();
					});

					t.fullscreenEditor.render();

					t.fullscreenElement = new tinymce.dom.Element('mce_fullscreen_container');
					t.fullscreenElement.update();
                                        
                                        // Force IE9 to keep container span to the left
                                        var p = document.getElementById('mce_fullscreen_parent');
                                        p.style.display = "block";
                                        p.style.position = "absolute";
                                        p.style.top = "0px"
                                        p.style.left = "0px"
                                        
					//document.body.overflow = 'hidden';
                                        tinymce.dom.Event.add(DOM.win, 'resize', t.resizeFunc);
                                        
				}
			});

			// Register buttons
			ed.addButton('fullscreen', {title : 'fullscreen.desc', cmd : 'mceFullScreen'});

			ed.onNodeChange.add(function(ed, cm) {
				cm.setActive('fullscreen', ed.getParam('fullscreen_is_enabled'));
			});
		},

		getInfo : function() {
			return {
				longname : 'Fullscreen',
				author : 'Moxiecode Systems AB',
				authorurl : 'http://tinymce.moxiecode.com',
				infourl : 'http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/fullscreen',
				version : tinymce.majorVersion + "." + tinymce.minorVersion
			};
		}
	});

	// Register plugin
	tinymce.PluginManager.add('fullscreen', tinymce.plugins.FullScreenPlugin);
})();