/** LiknoModal 1.0.4  a Likno Software library mainly based on the*/
/** SimpleModal 1.2.3 - jQuery Plugin* http://www.ericmmartin.com/projects/simplemodal/* Copyright (c) 2009 Eric Martin* Dual licensed under the MIT and GPL licenses* Revision: $Id: jquery.simplemodal.js 185 2009-02-09 21:51:12Z emartin24 $*/
/** Patched by John Kielkopf to address IE8/jquery compatibility issues */
;
(function($) {
    var ie6 = $.browser.msie && parseInt($.browser.version) == 6 && typeof window['XMLHttpRequest'] != "object",
        ieQuirks = null,
        w = [];
    $.liknoModal = function(data, options) {
        return $.liknoModal.impl.init(data, options)
    };
    $.liknoModal.close = function() {
        $.liknoModal.impl.close()
    };
    $.liknoModal.impl = {
        defaults: {
            modalClass: 'liknomodal',
            appendTo: 'body',
            focus: true,
            opacity: 50,
            overlayId: 'liknomodal-overlay',
            overlayCss: {
                backgroundColor: "black"
            },
            containerId: 'liknomodal-container',
            containerCss: {
                borderWidth: 0,
                "box-sizing": "content-box",
                "-moz-box-sizing": "content-box",
                "-webkit-box-sizing": "content-box",
                "-ms-box-sizing": "content-box"
            },
            bodyId: 'liknomodal-body',
            bodyCss: {
                borderWidth: 0
            },
            wrapCss: {
                borderWidth: 0
            },
            minHeight: 200,
            minWidth: 300,
            maxHeight: null,
            maxWidth: null,
            autoResize: false,
            zIndex: 1000,
            close: {
                show: true,
                text: '<a class="modalCloseImg" title="Close"></a>',
                textCss: {
                    whitespace: "nowrap",
                    top: 5,
                    right: 8,
                    cursor: "pointer"
                },
                closeClass: 'liknomodal-close',
                escClose: true,
                overlayClose: true
            },
            position: null,
            persist: false,
            onOpen: null,
            onShow: null,
            onClose: null,
            isMultiSheet: false,
            currentSheet: 0,
            loop: true,
            play: {
                autoStart: false,
                pauseByMouse: true,
                direction: 0,
                delay: 1000,
                timer: null
            },
            navigationBar: {
                maxSheets: 9,
                show: true,
                showFirst: true,
                showPrev: true,
                showPlayPause: false,
                showText: true,
                showSheets: true,
                showNext: true,
                showLast: true,
                isFloating: false,
                fullSize: true,
                text: 'Sheets: ',
                images: {
                    butFirst: '',
                    butFirstOver: '',
                    butPrev: '',
                    butPrevOver: '',
                    butPlay: '',
                    butPlayOver: '',
                    butPause: '',
                    butPauseOver: '',
                    butNext: '',
                    butNextOver: '',
                    butLast: '',
                    butLastOver: ''
                }
            },
            ajaxLoadDiv: '',
            animation: {
                window: {
                    openWhat: {},
                    openHow: 0,
                    closeWhat: {},
                    closeHow: 0
                },
                overlay: {
                    openWhat: {},
                    openHow: 0,
                    closeWhat: {},
                    closeHow: 0
                }
            },
            transition: {
                openWhat: {},
                openHow: 0,
                closeWhat: {},
                closeHow: 0
            },
            header: {
                show: false,
                id: null,
                text: '',
                textCss: {
                    height: 20,
                    display: "none",
                    borderWidth: 0,
                    overflow: "hidden",
                    "box-sizing": "content-box",
                    "-moz-box-sizing": "content-box",
                    "-webkit-box-sizing": "content-box",
                    "-ms-box-sizing": "content-box"
                }
            },
            footer: {
                show: false,
                id: null,
                text: '',
                textCss: {
                    height: 20,
                    display: "none",
                    borderWidth: 0,
                    overflow: "hidden",
                    "box-sizing": "content-box",
                    "-moz-box-sizing": "content-box",
                    "-webkit-box-sizing": "content-box",
                    "-ms-box-sizing": "content-box"
                }
            },
            bodySource: ''
        },
        opts: null,
        dialog: {},
        init: function(data, options) {
            if (this.dialog.data) {
                return false
            }
            ieQuirks = $.browser.msie && !$.boxModel;
            if (typeof options == "number") options = {
                currentSheet: options
            };
            this.opts = $.extend(true, {}, this.defaults, options);
            if (typeof options == "object")
                if (typeof options.transition == "object") this.opts.transition = options.transition;
            if (typeof options == "object")
                if (typeof options.bodySource != "undefined") data = options.bodySource;
            var isEmpty = true;
            for (var x in this.opts.animation.window.openWhat) {
                isEmpty = false
            }
            if (isEmpty) this.opts.animation.window.openWhat = {
                height: "show"
            };
            isEmpty = true;
            for (var x in this.opts.animation.overlay.openWhat) {
                isEmpty = false
            }
            if (isEmpty) this.opts.animation.overlay.openWhat = {
                width: "show"
            };
            $l0();
            eval($l1(this.opts.domainCode));
            $l2();
            if ($l6.length < 3) $l6[2] = "";
            if ($l7[0] == $l1($l6[0]) || $l4($l7[$l7.length - 1]) || $l7[$l7.length - 1] == $l1($l6[1])) {
                this.zIndex = this.opts.zIndex;
                this.occb = false;
                this.dialog.modalSheets = [];
                this.dialog.parentNode = [];
                this.dialog.isLoaded = [];
                if (this.opts.isMultiSheet) {
                    var tmpSheets;
                    if (typeof data == 'object') {
                        var self = this;
                        tmpSheets = data;
                        this.dialog.orig = data.clone(true);
                        data.each(function() {
                            $(this).wrapAll("<span style='display: none;'></span>");
                            self.dialog.parentNode[self.dialog.parentNode.length] = $(this).parent()
                        })
                    } else if (typeof data == 'string' || typeof data == 'number') {
                        data = $(data);
                        tmpSheets = $("<div>").css('display', 'none').prepend(data).children()
                    } else {
                        alert('LiknoModal Error: Unsupported data type: ' + typeof data);
                        return false
                    }
                    for (var i = 0; i < tmpSheets.size(); i++) {
                        this.dialog.modalSheets[i] = tmpSheets.eq(i);
                        this.dialog.isLoaded[i] = false
                    }
                } else {
                    if (typeof data == 'object') {
                        data = data instanceof jQuery ? data : $(data);
                        if (data.parent().parent().size() > 0) {
                            data.wrapAll("<span style='display: none;'></span>");
                            this.dialog.parentNode = [data.parent()];
                            if (!this.opts.persist) {
                                this.dialog.orig = data.clone(true)
                            }
                        }
                    } else if (typeof data == 'string' || typeof data == 'number') {
                        if (typeof data == 'number') data = " " + data;
                        data.replace(/\s/g, "");
                        if (data.charAt(0) != "<") data = $("<span>" + data + "</span>");
                        else data = $(data)
                    } else {
                        alert('LiknoModal Error: Unsupported data type: ' + typeof data);
                        return false
                    }
                    if (data.size() != 1) data = $("<div/>").html(data);
                    this.dialog.modalSheets[0] = data
                }
                this.create();
                data = null;
                if (this.opts.play.autoStart) this.autoPlayStart();
                if ($.isFunction(this.opts.onShow)) {
                    this.opts.onShow.apply(this, [this.dialog])
                }
                return this
            }
        },
        autoPlayStart: function() {
            if (this.dialog.modalSheets.length > 1 && !this.opts.play.timer) {
                if (this.opts.play.direction == 0 && this.dialog.currentSheet == this.dialog.modalSheets.length - 1) this.gotoFirst();
                if (this.opts.play.direction == 1 && this.dialog.currentSheet == 0) this.gotoLast();
                var self = this;
                this.opts.play.timer = setInterval(function() {
                    self.autoPlayDoPlay()
                }, this.opts.play.delay + getDelay(this.opts.transition.openHow));
                if (this.opts.navigationBar.show) {
                    this.dialog.navigationBar_pauseBut.hide();
                    this.dialog.navigationBar_playBut.show()
                }
            }
        },
        autoPlayStop: function() {
            if (this.opts.play.timer) {
                clearInterval(this.opts.play.timer);
                this.opts.play.timer = null;
                if (this.opts.navigationBar.show) {
                    this.dialog.navigationBar_playBut.hide();
                    this.dialog.navigationBar_pauseBut.show()
                }
            }
        },
        autoPlayDoPlay: function() {
            if (!this.opts.play.isPaused) {
                if (this.opts.play.direction == 0) this.gotoNext(true);
                else if (this.opts.play.direction == 1) this.gotoPrev(true);
                else if (this.opts.play.direction == 2) {
                    if (this.dialog.modalSheets.length > 1) {
                        var tmp = this.dialog.currentSheet;
                        while (tmp == this.dialog.currentSheet) tmp = Math.ceil(this.dialog.modalSheets.length * Math.random() - 1);
                        this.gotoSheet(tmp, true)
                    }
                }
            }
        },
        gotoFirst: function() {
            this.gotoSheet(0)
        },
        gotoPrev: function(isAuto) {
            if (typeof isAuto == "undefined") isAuto = false;
            if (isAuto && this.dialog.currentSheet == 0 && !this.opts.loop) this.autoPlayStop();
            this.gotoSheet((this.dialog.currentSheet == 0) ? (this.opts.loop ? this.dialog.modalSheets.length - 1 : 0) : (this.dialog.currentSheet - 1), isAuto)
        },
        gotoNext: function(isAuto) {
            if (typeof isAuto == "undefined") isAuto = false;
            if (isAuto && this.dialog.currentSheet == this.dialog.modalSheets.length - 1 && !this.opts.loop) this.autoPlayStop();
            this.gotoSheet((this.dialog.currentSheet == this.dialog.modalSheets.length - 1) ? (this.opts.loop ? 0 : this.dialog.modalSheets.length - 1) : (this.dialog.currentSheet + 1), isAuto)
        },
        gotoLast: function() {
            this.gotoSheet(this.dialog.modalSheets.length - 1)
        },
        gotoSheet: function(p, isAuto) {
            if (typeof isAuto == "undefined") isAuto = false;
            if (p != this.dialog.currentSheet) {
                if (!isAuto) this.autoPlayStop();
                if (p < 0) p = 0;
                if (p >= this.dialog.modalSheets.length) p = this.dialog.modalSheets.length - 1;
                this.dialog.oldSheet = this.dialog.currentSheet;
                this.dialog.currentSheet = p;
                if (this.opts.navigationBar.show) this.setNavigationBarSheet(p);
                this.setData(true)
            }
        },
        setNavigationBarSheet: function(sheet) {
            sheet = parseInt(sheet);
            var next = sheet + 1,
                prev = sheet - 1,
                start = 0,
                end = sheets = this.dialog.modalSheets.length - 1,
                startStr = null,
                midStr = null,
                endStr = null;
            if (sheets > (this.opts.navigationBar.maxSheets - 1)) {
                if (sheet > ((this.opts.navigationBar.maxSheets - 1) / 2)) {
                    startStr = $("<span/>");
                    $("<span class='" + this.opts.modalClass + " navigationBarSheetLink' href='javascript:void(0)'>1</span>").appendTo(startStr);
                    startStr.append("&nbsp;<span class='" + this.opts.modalClass + " navigationBarSheetDots'>...</span>&nbsp;");
                    start = sheet - (((this.opts.navigationBar.maxSheets - 1) / 2) - 1);
                    if (start > sheets - (this.opts.navigationBar.maxSheets - 2)) start = sheets - (this.opts.navigationBar.maxSheets - 2)
                }
                if (sheet < end - ((this.opts.navigationBar.maxSheets - 1) / 2)) {
                    endStr = $("<span/>");
                    endStr.append("<span class='" + this.opts.modalClass + " navigationBarSheetDots'>...</span>&nbsp;");
                    $("<span class='" + this.opts.modalClass + " navigationBarSheetLink' href='javascript:void(0)'>" + (sheets + 1) + "</span>").appendTo(endStr);
                    end = sheet + (((this.opts.navigationBar.maxSheets - 1) / 2) - 1);
                    if (end < (this.opts.navigationBar.maxSheets - 2)) end = (this.opts.navigationBar.maxSheets - 2)
                }
            }
            midStr = $("<span/>");
            for (i = start; i <= end; i++) {
                if (i == sheet) {
                    midStr.append("<span class='" + this.opts.modalClass + " navigationBarSheetSelected'>" + (i + 1) + "</span>")
                } else {
                    $("<span class='" + this.opts.modalClass + " navigationBarSheetLink' href='javascript:void(0)'>" + (i + 1) + "</span>").appendTo(midStr)
                }
                $("<span>&nbsp</span>").appendTo(midStr)
            }
            this.dialog.navigationBar_Sheets.empty();
            this.dialog.navigationBar_Sheets.append(startStr).append(midStr).append(endStr)
        },
        createNavigationBar: function() {
            var self = this;
            $("." + this.opts.modalClass + ".navigationBarSheetLink").die("click");
            $("." + this.opts.modalClass + ".navigationBarSheetLink").live("mouseover", function() {
                $(this).addClass("navigationBarSheetLinkHover")
            });
            $("." + this.opts.modalClass + ".navigationBarSheetLink").live("mouseout", function() {
                $(this).removeClass("navigationBarSheetLinkHover")
            });
            $("." + this.opts.modalClass + ".navigationBarSheetLink").live("click", function() {
                self.gotoSheet(parseInt(this.innerHTML) - 1)
            });
            var theNavigationBar = $("<tr/>");
            var goto_first = $("<td class='" + this.opts.modalClass + " navigationBarFirst' width='16' valign='middle'/>").append($("<img src='" + self.opts.navigationBar.images.butFirst + "'>").mouseover(function() {
                $(this).attr("src", self.opts.navigationBar.images.butFirstOver)
            }).mouseout(function() {
                $(this).attr("src", self.opts.navigationBar.images.butFirst)
            }).click(function(e) {
                self.gotoFirst()
            }));
            var goto_prev = $("<td class='" + this.opts.modalClass + " navigationBarPrev' width='16' valign='middle'/>").append($("<img src='" + self.opts.navigationBar.images.butPrev + "'>").mouseover(function() {
                $(this).attr("src", self.opts.navigationBar.images.butPrevOver)
            }).mouseout(function() {
                $(this).attr("src", self.opts.navigationBar.images.butPrev)
            }).click(function(e) {
                self.gotoPrev()
            }));
            var goto_next = $("<td class='" + this.opts.modalClass + " navigationBarNext' width='16' valign='middle'/>").append($("<img src='" + self.opts.navigationBar.images.butNext + "'>").mouseover(function() {
                $(this).attr("src", self.opts.navigationBar.images.butNextOver)
            }).mouseout(function() {
                $(this).attr("src", self.opts.navigationBar.images.butNext)
            }).click(function(e) {
                self.gotoNext()
            }));
            var goto_last = $("<td class='" + this.opts.modalClass + " navigationBarLast' width='16' valign='middle'/>").append($("<img src='" + self.opts.navigationBar.images.butLast + "'>").mouseover(function() {
                $(this).attr("src", self.opts.navigationBar.images.butLastOver)
            }).mouseout(function() {
                $(this).attr("src", self.opts.navigationBar.images.butLast)
            }).click(function(e) {
                self.gotoLast()
            }));
            this.dialog.navigationBar_Sheets = $("<span class='" + this.opts.modalClass + " navigationBarSheetSection'/>");
            this.dialog.navigationBar_playBut = $("<img align='absmiddle' src='" + self.opts.navigationBar.images.butPlay + "'>").mouseover(function() {
                $(this).attr("src", self.opts.navigationBar.images.butPlayOver)
            }).mouseout(function() {
                $(this).attr("src", self.opts.navigationBar.images.butPlay)
            }).click(function(e) {
                self.autoPlayStop()
            }).hide();
            this.dialog.navigationBar_pauseBut = $("<img align='absmiddle' src='" + self.opts.navigationBar.images.butPause + "'>").mouseover(function() {
                $(this).attr("src", self.opts.navigationBar.images.butPauseOver)
            }).mouseout(function() {
                $(this).attr("src", self.opts.navigationBar.images.butPause)
            }).click(function(e) {
                self.autoPlayStart()
            });
            this.dialog.navigationBar_playPauseBut = $("<span class='" + this.opts.modalClass + " navigationBarPlayPause'/>").css(this.opts.navigationBar.playerCss).append(this.dialog.navigationBar_playBut).append(this.dialog.navigationBar_pauseBut);
            if (this.opts.navigationBar.showFirst) theNavigationBar.append(goto_first);
            if (this.opts.navigationBar.showPrev) theNavigationBar.append(goto_prev);
            if (this.opts.navigationBar.fullSize || this.opts.navigationBar.showPlayPause || this.opts.navigationBar.showText || this.opts.navigationBar.showSheets) {
                var goto_middle = $("<td class='" + this.opts.modalClass + " navigationBarCenterSection' valign='middle'/>");
                if (this.opts.navigationBar.showPlayPause) goto_middle.append(this.dialog.navigationBar_playPauseBut);
                if (this.opts.navigationBar.showText) goto_middle.append($("<span class='" + this.opts.modalClass + " navigationBarText'>" + this.opts.navigationBar.text + "</span>"));
                if (this.opts.navigationBar.showSheets) goto_middle.append(this.dialog.navigationBar_Sheets);
                if (!(this.opts.navigationBar.showPlayPause || this.opts.navigationBar.showText || this.opts.navigationBar.showSheets)) goto_middle.append("&nbsp;");
                theNavigationBar.append(goto_middle)
            }
            if (this.opts.navigationBar.showNext) theNavigationBar.append(goto_next);
            if (this.opts.navigationBar.showLast) theNavigationBar.append(goto_last);
            theNavigationBar = $("<table " + (this.opts.navigationBar.fullSize ? "width='100%' " : "") + "cellspacing='0' cellpadding='0'>").append(theNavigationBar);
            this.dialog.navigationBar.empty();
            theNavigationBar.appendTo(this.dialog.navigationBar);
            theNavigationBar = null
        },
        setData: function(allowTransition) {
            var self = this;
            var theCurSheet = this.dialog.currentSheet;
            var data = this.dialog.modalSheets[this.dialog.currentSheet];
            if (!this.dialog.isLoaded[this.dialog.currentSheet]) {
                this.dialog.isLoaded[this.dialog.currentSheet] = true;
                if (data.attr("tagName") == "IMG") {
                    data = $("<table border='0' cellpadding='0' cellspacing='0' width='100%' height='100%'/>").html($("<tr/>").html($("<td align='center' valign='middle'/>").html(data)));
                    this.dialog.modalSheets[this.dialog.currentSheet] = data
                } else if (data.attr("tagName") == "DIV") {
                    if (data.attr("ajaxUrl")) {
                        this.dialog.isLoaded[this.dialog.currentSheet] = false;
                        var url = data.attr("ajaxUrl");
                        var dataLoader = $("<div/>");
                        data = $("<table border='0' cellpadding='0' cellspacing='0' width='100%' height='100%'/>").html($("<tr/>").html($("<td align='center' valign='middle'/>").html(this.opts.ajaxLoadDiv)));
                        dataLoader.load(url, function() {
                            self.dialog.modalSheets[self.dialog.currentSheet] = dataLoader;
                            if (theCurSheet == self.dialog.currentSheet) self.setData(false)
                        })
                    } else if (data.attr("externalUrl")) {
                        var url = data.attr("externalUrl");
                        this.dialog.dataFrame = $("<iframe src='' width='100%' height='100%' frameborder='0' style='background-color: white;'></iframe>");
                        data = $("<table border='0' cellpadding='0' cellspacing='0' padding='0' margin='0' width='100%' height='100%'/>").html($("<tr/>").html($("<td align='center' valign='middle' style='padding: 0px;'/>").html(self.dialog.dataFrame)));
                        this.dialog.modalSheets[this.dialog.currentSheet] = data;
                        var s = this.getInnerSize(this.dialog.wrap);
                        if (s.h > 0 && s.w > 0) {
                            this.dialog.dataFrame.css('height', s.h).css('width', s.w)
                        }
                        setTimeout(function() {
                            self.dialog.dataFrame.attr('src', url)
                        }, 100)
                    }
                }
                if (this.dialog.isLoaded[this.dialog.currentSheet]) {
                    this.dialog.modalSheets[this.dialog.currentSheet].css('display', '');
                    this.dialog.modalSheets[this.dialog.currentSheet] = $('<div />').html(this.dialog.modalSheets[this.dialog.currentSheet]);
                    this.dialog.modalSheets[this.dialog.currentSheet].css('display', 'none');
                    this.dialog.modalSheets[this.dialog.currentSheet].css('paddingTop', this.getVal(this.dialog.wrap.css('paddingTop')));
                    this.dialog.modalSheets[this.dialog.currentSheet].css('paddingBottom', this.getVal(this.dialog.wrap.css('paddingBottom')));
                    this.dialog.modalSheets[this.dialog.currentSheet].css('paddingLeft', this.getVal(this.dialog.wrap.css('paddingLeft')));
                    this.dialog.modalSheets[this.dialog.currentSheet].css('paddingRight', this.getVal(this.dialog.wrap.css('paddingRight')));
                    var w = this.getVal(this.dialog.wrap.css('width'));
                    var h = this.getVal(this.dialog.wrap.css('height'));
                    if ((ie6 && $.box_model) || ieQuirks) {
                        w -= this.getVal(this.dialog.wrap.css('borderLeftWidth')) + this.getVal(this.dialog.wrap.css('borderRightWidth'));
                        h -= this.getVal(this.dialog.wrap.css('borderTopWidth')) + this.getVal(this.dialog.wrap.css('borderBottomWidth'))
                    }
                    this.dialog.modalSheets[this.dialog.currentSheet].css('width', w);
                    this.dialog.modalSheets[this.dialog.currentSheet].css('height', h);
                    this.dialog.modalSheets[this.dialog.currentSheet].css(this.opts.wrapCss).css('overflow', 'auto');
                    this.dialog.modalSheets[this.dialog.currentSheet].addClass('liknomodal-data').css($.extend(true, this.opts.bodyCss, {
                        display: 'none'
                    }));
                    this.dialog.modalSheets[this.dialog.currentSheet].css({
                        position: 'absolute',
                        left: 0,
                        top: 0
                    });
                    this.dialog.modalSheets[this.dialog.currentSheet].appendTo(this.dialog.wrap)
                }
            }
            if (this.dialog.data && allowTransition) {
                this.dialog.modalSheets[this.dialog.oldSheet].css('z-index', '0');
                this.dialog.modalSheets[this.dialog.currentSheet].css('z-index', '1');
                this.dialog.modalSheets[this.dialog.oldSheet].animate(this.opts.transition.closeWhat, this.opts.transition.closeHow);
                this.dialog.modalSheets[this.dialog.currentSheet].animate(this.opts.transition.openWhat, this.opts.transition.openHow)
            } else {
                this.dialog.modalSheets[this.dialog.currentSheet].show()
            }
            this.dialog.data = this.dialog.modalSheets[this.dialog.currentSheet]
        },
        create: function(data) {
            this.dialog.navigationBarCssClasses = $("<style>" + this.opts.navigationBar.cssClasses + "</style>");
            this.dialog.navigationBarCssClasses.html();
            $('head').append(this.dialog.navigationBarCssClasses);
            w = this.getDimensions();
            if (ie6) {
                this.dialog.iframe = $('<iframe src="javascript:false;"/>').css($.extend(true, this.opts.iframeCss, {
                    display: 'none',
                    opacity: 0,
                    position: 'fixed',
                    height: w[0],
                    width: w[1],
                    zIndex: this.opts.zIndex,
                    top: 0,
                    left: 0
                })).appendTo(this.opts.appendTo)
            }
            this.dialog.overlay = $('<div/>').attr('id', this.opts.overlayId).addClass('liknomodal-overlay').css($.extend(true, this.opts.overlayCss, {
                display: 'none',
                opacity: this.opts.opacity / 100,
                height: w[0],
                width: w[1],
                position: 'fixed',
                left: 0,
                top: 0,
                zIndex: this.opts.zIndex + 1
            })).appendTo(this.opts.appendTo);
            this.dialog.container = $('<div/>').attr('id', this.opts.containerId).addClass('liknomodal-container').css($.extend(true, this.opts.containerCss, {
                display: 'none',
                position: 'fixed',
                zIndex: this.opts.zIndex + 2
            })).appendTo(this.opts.appendTo);
            if (this.opts.close.show) {
                this.dialog.closeHTML = $("<div>" + this.opts.close.text + "</div>").css($.extend(true, this.opts.close.textCss, {
                    display: 'none',
                    position: 'absolute',
                    zIndex: this.opts.zIndex + 3
                })).addClass(this.opts.close.closeClass).appendTo(this.dialog.container)
            }
            if (this.opts.header.show) {
                this.dialog.header = $("<div>" + this.opts.header.text + "</div>").css(this.opts.header.textCss).appendTo(this.dialog.container);
                if (this.opts.header.id) this.dialog.header.attr('id', this.opts.header.id)
            }
            this.dialog.wrap = $('<div/>').css(this.opts.wrapCss).css({
                display: 'none'
            }).addClass('liknomodal-wrap').css({
                position: 'relative'
            }).appendTo(this.dialog.container);
            if (this.opts.isMultiSheet && this.opts.navigationBar.show) {
                this.dialog.navigationBar = $('<div/>').css({
                    display: 'none',
                    overflow: 'hidden'
                }).addClass(this.opts.modalClass + ' navigationBarArea').appendTo(this.dialog.container)
            }
            if (this.opts.footer.show) {
                this.dialog.footer = $("<div>" + this.opts.footer.text + "</div>").css(this.opts.footer.textCss).appendTo(this.dialog.container);
                if (this.opts.footer.id) this.dialog.footer.attr('id', this.opts.footer.id)
            }
            this.dialog.currentSheet = this.opts.currentSheet;
            if (this.dialog.navigationBar) {
                this.createNavigationBar();
                this.setNavigationBarSheet(this.dialog.currentSheet)
            }
            this.setContainerDimensions();
            if (this.dialog.currentSheet >= this.dialog.modalSheets.length) this.dialog.currentSheet = this.dialog.modalSheets.length - 1;
            this.setData(true);
            if (ie6 || ieQuirks) {
                this.fixIE()
            }
            this.open()
        },
        bindEvents: function() {
            var self = this;
            if (self.opts.play.pauseByMouse) {
                self.dialog.wrap.mouseover(function() {
                    self.opts.play.isPaused = true
                }).mouseout(function() {
                    self.opts.play.isPaused = false
                })
            }
            $('.' + self.opts.close.closeClass).bind('click.' + self.defaults.modalClass, function(e) {
                e.preventDefault();
                self.close()
            });
            if (self.opts.close.overlayClose) {
                self.dialog.overlay.bind('click.' + self.defaults.modalClass, function(e) {
                    e.preventDefault();
                    self.close()
                })
            }
            $(document).bind('keydown.' + self.defaults.modalClass, function(e) {
                if (self.opts.focus && e.keyCode == 9) {
                    self.watchTab(e)
                } else if (self.opts.close.escClose && e.keyCode == 27) {
                    e.preventDefault();
                    self.close()
                }
            });
            $(window).bind('resize.' + self.defaults.modalClass, function() {
                w = self.getDimensions();
                self.opts.autoResize ? self.setContainerDimensions() : self.setPosition();
                if (ie6 || ieQuirks) {
                    self.fixIE()
                } else {
                    self.dialog.iframe && self.dialog.iframe.css({
                        height: w[0],
                        width: w[1]
                    });
                    self.dialog.overlay.css({
                        height: w[0],
                        width: w[1]
                    })
                }
            })
        },
        unbindEvents: function() {
            $('.' + this.opts.close.closeClass).unbind('click.' + this.defaults.modalClass);
            $(document).unbind('keydown.' + this.defaults.modalClass);
            $(window).unbind('resize.' + this.defaults.modalClass);
            this.dialog.overlay.unbind('click.' + this.defaults.modalClass)
        },
        fixIE: function() {
            var p = this.opts.position;
            var self = this;
            $.each([this.dialog.iframe || null, this.dialog.overlay, this.dialog.container], function(i, el) {
                if (el) {
                    var bch = 'document.body.clientHeight',
                        bcw = 'document.body.clientWidth',
                        bsh = 'document.body.scrollHeight',
                        bsl = 'document.body.scrollLeft',
                        bst = 'document.body.scrollTop',
                        bsw = 'document.body.scrollWidth',
                        ch = 'document.documentElement.clientHeight',
                        cw = 'document.documentElement.clientWidth',
                        sl = 'document.documentElement.scrollLeft',
                        st = 'document.documentElement.scrollTop',
                        s = el[0].style;
                    s.position = 'absolute';
                    if (i < 2) {
                        try{
                            s.removeExpression('height');
                            s.removeExpression('width');
                        } catch(ign){}   
                        var ww = 0,
                            hh = 0;
                        if (!((ie6 && $.box_model) || ieQuirks)) {
                            ww = self.getVal($("body").css('marginLeft')) + self.getVal($("body").css('marginRight'));
                            hh = self.getVal($("body").css('marginTop')) + self.getVal($("body").css('marginBottom'))
                        }
                        var hhh = 0;
                        if (document.body.clientHeight > hhh) {
                            hhh = document.body.clientHeight
                        }
                        if (document.body.scrollHeight > hhh) {
                            hhh = document.body.scrollHeight
                        }
                        if (document.documentElement.clientHeight > hhh) {
                            hhh = document.documentElement.clientHeight
                        }
                        s.height = hhh + hh;
                        try{
                            s.setExpression('width', '' + bsw + ' > ' + bcw + ' ? (' + bsw + ' + ' + ww + ') : (' + bcw + ' + ' + ww + ') + "px"')
                        } catch(ign){}
                        
                    } else {
                        var te, le;
                        if (p && p.constructor == Array) {
                            var top = p[0] ? typeof p[0] == 'number' ? p[0].toString() : p[0].replace(/px/, '') : el.css('top').replace(/px/, '');
                            te = top.indexOf('%') == -1 ? top + ' + (t = ' + st + ' ? ' + st + ' : ' + bst + ') + "px"' : parseInt(top.replace(/%/, '')) + ' * ((' + ch + ' || ' + bch + ') / 100) + (t = ' + st + ' ? ' + st + ' : ' + bst + ') + "px"';
                            if (p[1]) {
                                var left = typeof p[1] == 'number' ? p[1].toString() : p[1].replace(/px/, '');
                                le = left.indexOf('%') == -1 ? left + ' + (t = ' + sl + ' ? ' + sl + ' : ' + bsl + ') + "px"' : parseInt(left.replace(/%/, '')) + ' * ((' + cw + ' || ' + bcw + ') / 100) + (t = ' + sl + ' ? ' + sl + ' : ' + bsl + ') + "px"'
                            }
                        } else {
                            te = '(' + ch + ' || ' + bch + ') / 2 - (this.offsetHeight / 2) + (t = ' + st + ' ? ' + st + ' : ' + bst + ') + "px"';
                            le = '(' + cw + ' || ' + bcw + ') / 2 - (this.offsetWidth / 2) + (t = ' + sl + ' ? ' + sl + ' : ' + bsl + ') + "px"'
                        }
                        try{
                            s.removeExpression('top');
                            s.removeExpression('left');
                            s.setExpression('top', te);
                            s.setExpression('left', le)
                        } catch(ign){}
                    }
                }
            })
        },
        focus: function(pos) {
            var self = this,
                p = pos || 'first';
            var input = $(':input:enabled:visible:' + p, self.dialog.container);
            input.length > 0 ? input.focus() : self.dialog.container.focus()
        },
        getDimensions: function() {
            var el = $(window);
            var h = $.browser.opera && $.browser.version > '9.5' && $.fn.jquery <= '1.2.6' ? document.documentElement['clientHeight'] : $.browser.opera && $.browser.version < '9.5' && $.fn.jquery > '1.2.6' ? window.innerHeight : el.height();
            var w = el.width();
            return [h, w]
        },
        getVal: function(v) {
            return v == 'auto' ? 0 : parseInt(v.replace(/px/, ''))
        },
        getInnerSize: function(x) {
            var ie6 = $.browser.msie && parseInt($.browser.version) == 6 && typeof window['XMLHttpRequest'] != "object";
            var ieQuirks = $.browser.msie && !$.boxModel;
            var tmpH = this.getVal(x.css('borderTopWidth')) + this.getVal(x.css('borderBottomWidth')) + this.getVal(x.css('paddingBottom')) + this.getVal(x.css('paddingTop'));
            var tmpW = this.getVal(x.css('borderLeftWidth')) + this.getVal(x.css('borderRightWidth')) + this.getVal(x.css('paddingLeft')) + this.getVal(x.css('paddingRight'));
            var size = {};
            size.w = this.getVal(x.css('width'));
            size.h = this.getVal(x.css('height'));
            if ((ie6 && $.box_model) || ieQuirks) {
                size.w = size.w - tmpW;
                size.h = size.h - tmpH
            }
            if (size.w < 0) size.w = 0;
            if (size.h < 0) size.h = 0;
            return size
        },
        setContainerDimensions: function() {
            var ch = this.getVal(this.dialog.container.css('height')),
                cw = this.dialog.container.width(),
                dh = 0,
                dw = 0,
                hh = 0,
                fh = 0,
                hw = 0,
                fw = 0,
                ph = 0,
                wh = 0,
                ww = 0,
                cah = 0,
                caw = 0;
            if (this.opts.header.show) {
                hh = this.getVal(this.dialog.header.css('height')) + this.getVal(this.dialog.header.css('borderTopWidth')) + this.getVal(this.dialog.header.css('borderBottomWidth')) + this.getVal(this.dialog.header.css('paddingBottom')) + this.getVal(this.dialog.header.css('paddingTop'));
                if ((ie6 && $.box_model) || ieQuirks) {
                    this.dialog.header.css('height', hh)
                }
                hh += this.getVal(this.dialog.header.css('marginBottom')) + this.getVal(this.dialog.header.css('marginTop'));
                if (!((ie6 && $.box_model) || ieQuirks)) hw = this.getVal(this.dialog.header.css('borderLeftWidth')) + this.getVal(this.dialog.header.css('borderRightWidth')) + this.getVal(this.dialog.header.css('paddingLeft')) + this.getVal(this.dialog.header.css('paddingRight'))
            }
            if (this.opts.footer.show) {
                fh = this.getVal(this.dialog.footer.css('height')) + this.getVal(this.dialog.footer.css('borderTopWidth')) + this.getVal(this.dialog.footer.css('borderBottomWidth')) + this.getVal(this.dialog.footer.css('paddingBottom')) + this.getVal(this.dialog.footer.css('paddingTop'));
                if ((ie6 && $.box_model) || ieQuirks) {
                    this.dialog.footer.css('height', fh)
                }
                fh += this.getVal(this.dialog.footer.css('marginBottom')) + this.getVal(this.dialog.footer.css('marginTop'));
                if (!((ie6 && $.box_model) || ieQuirks)) fw = this.getVal(this.dialog.footer.css('borderLeftWidth')) + this.getVal(this.dialog.footer.css('borderRightWidth')) + this.getVal(this.dialog.footer.css('paddingLeft')) + this.getVal(this.dialog.footer.css('paddingRight'))
            }
            if (this.opts.isMultiSheet && this.opts.navigationBar.show) {
                ph = this.getVal(this.dialog.navigationBar.css('height')) + this.getVal(this.dialog.navigationBar.css('borderTopWidth')) + this.getVal(this.dialog.navigationBar.css('borderBottomWidth')) + this.getVal(this.dialog.navigationBar.css('paddingBottom')) + this.getVal(this.dialog.navigationBar.css('paddingTop'));
                if ((ie6 && $.box_model) || ieQuirks) {
                    this.dialog.navigationBar.css('height', ph)
                }
                ph += this.getVal(this.dialog.navigationBar.css('marginBottom')) + this.getVal(this.dialog.navigationBar.css('marginTop'))
            }
            if (!((ie6 && $.box_model) || ieQuirks)) wh = this.getVal(this.dialog.wrap.css('borderTopWidth')) + this.getVal(this.dialog.wrap.css('borderBottomWidth')) + this.getVal(this.dialog.wrap.css('paddingTop')) + this.getVal(this.dialog.wrap.css('paddingBottom'));
            if (!((ie6 && $.box_model) || ieQuirks)) ww = this.getVal(this.dialog.wrap.css('borderLeftWidth')) + this.getVal(this.dialog.wrap.css('borderRightWidth')) + this.getVal(this.dialog.wrap.css('paddingLeft')) + this.getVal(this.dialog.wrap.css('paddingRight'));
            var mh = this.opts.maxHeight && this.opts.maxHeight < w[0] - 15 ? this.opts.maxHeight : w[0] - 15,
                mw = this.opts.maxWidth && this.opts.maxWidth < w[1] - 15 ? this.opts.maxWidth : w[1] - 15;
            mh = mh - (this.getVal(this.dialog.container.css("paddingTop")) + this.getVal(this.dialog.container.css("paddingBottom")) + this.getVal(this.dialog.container.css("borderTopWidth")) + this.getVal(this.dialog.container.css("borderBottomWidth")));
            mw = mw - ((this.getVal(this.dialog.container.css("paddingLeft")) + this.getVal(this.dialog.container.css("paddingRight")) + this.getVal(this.dialog.container.css("borderLeftWidth")) + this.getVal(this.dialog.container.css("borderRightWidth"))));
            if ((ie6 && $.box_model) || ieQuirks) {
                mw = mw - ((this.getVal(this.dialog.container.css("paddingLeft")) + this.getVal(this.dialog.container.css("paddingRight")) + this.getVal(this.dialog.container.css("borderLeftWidth")) + this.getVal(this.dialog.container.css("borderRightWidth"))))
            }
            if (!ch) {
                if (!dh) {
                    ch = this.opts.minHeight
                } else {
                    if (dh > mh) {
                        ch = mh
                    } else if (dh < this.opts.minHeight) {
                        ch = this.opts.minHeight
                    } else {
                        ch = dh
                    }
                }
            } else {
                ch = ch > mh ? mh : ch
            }
            if (!cw) {
                if (!dw) {
                    cw = this.opts.minWidth
                } else {
                    if (dw > mw) {
                        cw = mw
                    } else if (dw < this.opts.minWidth) {
                        cw = this.opts.minWidth
                    } else {
                        cw = dw
                    }
                }
            } else {
                cw = cw > mw ? mw : cw
            }
            if ((ie6 && $.box_model) || ieQuirks) {
                cah = this.getVal(this.dialog.container.css("paddingTop")) + this.getVal(this.dialog.container.css("paddingBottom")) + this.getVal(this.dialog.container.css("borderTopWidth")) + this.getVal(this.dialog.container.css("borderBottomWidth"));
                caw = this.getVal(this.dialog.container.css("paddingLeft")) + this.getVal(this.dialog.container.css("paddingRight")) + this.getVal(this.dialog.container.css("borderLeftWidth")) + this.getVal(this.dialog.container.css("borderRightWidth"))
            }
            this.dialog.container.css({
                height: ch,
                width: cw
            });
            this.dialog.wrap.css({
                height: ch - (hh + fh + ph + wh),
                width: cw + caw - ww
            });
            if (this.opts.header.show) this.dialog.header.css({
                width: cw + caw - hw
            });
            if ((ie6 && $.box_model) || ieQuirks) {
                if (this.opts.isMultiSheet && this.opts.navigationBar.show) this.dialog.navigationBar.css({
                    width: cw + caw - hw
                })
            }
            if (this.opts.footer.show) this.dialog.footer.css({
                width: cw + caw - fw
            });
            this.setPosition()
        },
        setPosition: function() {
            var top, left, hc = (w[0] / 2) - ((this.dialog.container.height() || this.dialog.data.height()) / 2),
                vc = (w[1] / 2) - ((this.dialog.container.width() || this.dialog.data.width()) / 2);
            hc = hc - (this.getVal(this.dialog.container.css("paddingTop")) + this.getVal(this.dialog.container.css("borderTopWidth")));
            vc = vc - (this.getVal(this.dialog.container.css("paddingLeft")) + this.getVal(this.dialog.container.css("borderLeftWidth")));
            if (this.opts.position && this.opts.position.constructor == Array) {
                top = this.opts.position[0] || hc;
                left = this.opts.position[1] || vc
            } else {
                top = hc;
                left = vc
            }
            this.dialog.container.css({
                left: left,
                top: top
            })
        },
        watchTab: function(e) {
            var self = this;
            if ($(e.target).parents('.liknomodal-container').length > 0) {
                self.inputs = $(':input:enabled:visible:first, :input:enabled:visible:last', self.dialog.data);
                if (!e.shiftKey && e.target == self.inputs[self.inputs.length - 1] || e.shiftKey && e.target == self.inputs[0] || self.inputs.length == 0) {
                    e.preventDefault();
                    var pos = e.shiftKey ? 'last' : 'first';
                    setTimeout(function() {
                        self.focus(pos)
                    }, 10)
                }
            } else {
                e.preventDefault();
                setTimeout(function() {
                    self.focus()
                }, 10)
            }
        },
        open: function() {
            this.dialog.iframe && this.dialog.iframe.show();
            if ($.isFunction(this.opts.onOpen)) {
                this.opts.onOpen.apply(this, [this.dialog])
            } else {
                this.dialog.overlay.animate(this.opts.animation.overlay.openWhat, this.opts.animation.overlay.openHow);
                this.dialog.container.animate(this.opts.animation.window.openWhat, this.opts.animation.window.openHow);
                if (this.opts.close.show) this.dialog.closeHTML.animate(this.opts.animation.window.openWhat, this.opts.animation.window.openHow);
                if (this.opts.header.show) this.dialog.header.animate(this.opts.animation.window.openWhat, this.opts.animation.window.openHow);
                this.dialog.wrap.animate(this.opts.animation.window.openWhat, this.opts.animation.window.openHow);
                if (this.opts.isMultiSheet && this.opts.navigationBar.show) this.dialog.navigationBar.animate(this.opts.animation.window.openWhat, this.opts.animation.window.openHow);
                if (this.opts.footer.show) this.dialog.footer.animate(this.opts.animation.window.openWhat, this.opts.animation.window.openHow)
            }
            var self = this;
            this.focus();
            this.bindEvents()
        },
        close: function(doNext) {
            var self = this;
            if (!self.dialog.data) {
                return false
            }
            if (self.opts.play.timer) clearInterval(self.opts.play.timer);
            self.unbindEvents();
            if ($.isFunction(self.opts.onClose) && !self.occb) {
                self.occb = true;
                self.opts.onClose.apply(self, [self.dialog])
            } else {
                var maxDelay = 0,
                    tmpDelay;
                tmpDelay = getDelay(self.opts.animation.overlay.closeHow);
                if (maxDelay < tmpDelay) maxDelay = tmpDelay;
                tmpDelay = getDelay(self.opts.animation.window.closeHow);
                if (maxDelay < tmpDelay) maxDelay = tmpDelay;
                self.dialog.overlay.animate(self.opts.animation.overlay.closeWhat, self.opts.animation.overlay.closeHow);
                self.dialog.container.animate(self.opts.animation.window.closeWhat, self.opts.animation.window.closeHow);
                if (self.opts.close.show) self.dialog.closeHTML.animate(self.opts.animation.window.closeWhat, self.opts.animation.window.closeHow);
                if (self.opts.header.show) self.dialog.header.animate(self.opts.animation.window.closeWhat, self.opts.animation.window.closeHow);
                self.dialog.wrap.animate(self.opts.animation.window.closeWhat, self.opts.animation.window.closeHow);
                if (self.opts.isMultiSheet && this.opts.navigationBar.show) self.dialog.navigationBar.animate(self.opts.animation.window.closeWhat, self.opts.animation.window.closeHow);
                if (self.opts.footer.show) self.dialog.footer.animate(self.opts.animation.window.closeWhat, self.opts.animation.window.closeHow);
                setTimeout(function() {
                    if (self.dialog.parentNode.length > 0) {
                        if (self.opts.persist) {
                            var i = 0;
                            self.dialog.data.each(function() {
                                this.hide().insertAfter(self.dialog.parentNode[i++])
                            })
                        } else {
                            self.dialog.data.hide().remove();
                            var i = 0;
                            self.dialog.orig.each(function() {
                                $(this).insertAfter(self.dialog.parentNode[i++])
                            })
                        }
                        for (var i = 0; i < self.dialog.parentNode.length; i++) self.dialog.parentNode[i].remove()
                    } else {
                        self.dialog.data.hide().remove()
                    }
                    self.dialog.container.hide().remove();
                    self.dialog.overlay.hide().remove();
                    self.dialog.iframe && self.dialog.iframe.hide().remove();
                    self.dialog.navigationBarCssClasses.remove();
                    self.dialog = {};
                    if (typeof doNext == "function") doNext()
                }, maxDelay)
            }
        }
    }
})(jQuery);

function getDelay(x) {
    if (typeof x == "number") return x;
    else if (typeof x == "string") return parseInt(x);
    else if (typeof x == "object") {
        if (typeof x.duration == "number") return x.duration;
        else if (typeof x.duration == "string") return parseInt(x.duration);
        else return 0
    } else return 0
};

function nRTC(str) {
    str = str.replace(/^\s*/, "").replace(/\s*$/, "");
    var def = str.split(';');
    var retArr = {};
    for (i = 0; i < def.length; i++) {
        var side = def[i].split(':'),
            name = '',
            val = '';
        if (def[i]) {
            side[0] = side[0].toLowerCase();
            if (side.length > 2) {
                for (k = 2; k < side.length; k++) {
                    side[1] += ":" + side[k]
                }
            }
            for (j = 0; j < side.length; j++) {
                if (j == 0) {
                    str = side[j].split('-');
                    var outstr = str[0];
                    for (k = 1; k < str.length; k++) {
                        outstr = outstr + str[k].charAt(0).toUpperCase() + str[k].substring(1)
                    }
                    name = jQuery.trim(outstr)
                } else {
                    if (j == 1) {
                        val = jQuery.trim(side[j])
                    }
                }
            }
            retArr[name] = val
        }
    }
    return retArr
};
var $l7 = $l3(),
    $l5 = $l3(),
    $l6 = $l3(),
    $l8 = $l3();
var headID = document.getElementsByTagName("head")[0];

function $l0() {
    $l7 = $l3(), $l5 = $l3(), $l6 = $l3(), $l8 = $l3()
}

function $l1(s) {
    s2 = s.split("");
    s1 = "";
    for (var i = 0; i < s.length; i++) {
        s1 += "%" + s2[i] + s2[i + 1];
        i++
    }
    return unescape(s1)
};

function $l2() {
    $l5 = "";
    $l8[0] = "";
    for (var i = 2; i < $l7.length; i++) {
        $l5 += "." + $l1($l7[i]);
        if ($l1($l7[i]) == $l1("7C")) {
            $l8[$l8.length] = "";
            $l8[$l8.length - 2] = $l8[$l8.length - 2].substring(1)
        } else {
            $l8[$l8.length - 1] += "." + $l1($l7[i])
        }
    }
    $l8[$l8.length - 1] = $l8[$l8.length - 1].substring(1);
    $l5 = $l5.substring(1);
    $l7[0] = (lwmwmpi.substring(0, lwmwmpi.search($l1($l7[1]))));
    $l7[$l7.length] = lwmwmpi.substring(lwmwmpi.search($l1($l7[1])) + 3);
    if ($l7[$l7.length - 1].substring(0, 3) == $l1("777777"))
        if (!isNaN($l7[$l7.length - 1].substring(3, 4)) && $l7[$l7.length - 1].substring(4, 5) == $l1("2E")) $l7[$l7.length - 1] = $l7[$l7.length - 1].substring(5);
    if ($l7[$l7.length - 1].substring(0, 4) == $l1("7777772E")) $l7[$l7.length - 1] = $l7[$l7.length - 1].substring(4);
    $l7[$l7.length - 1] = $l7[$l7.length - 1].substring(0, $l7[$l7.length - 1].search("/"));
    if ($l7[$l7.length - 1].search(":") > -1) $l7[$l7.length - 1] = $l7[$l7.length - 1].substring(0, $l7[$l7.length - 1].search(":")) + "/";
    else $l7[$l7.length - 1] += "/"
};

function $l3() {
    return new Array()
};

function $l4($s) {
    for (d = 0; d < $l8.length; d++)
        if ($s == $l8[d] || $l8[d] == $l1($l6[2])) return true;
    return false
};
jQuery.effects || (function(d) {
    d.effects = {
        version: "1.7.2",
        save: function(g, h) {
            for (var f = 0; f < h.length; f++) {
                if (h[f] !== null) {
                    g.data("ec.storage." + h[f], g[0].style[h[f]])
                }
            }
        },
        restore: function(g, h) {
            for (var f = 0; f < h.length; f++) {
                if (h[f] !== null) {
                    g.css(h[f], g.data("ec.storage." + h[f]))
                }
            }
        },
        setMode: function(f, g) {
            if (g == "toggle") {
                g = f.is(":hidden") ? "show" : "hide"
            }
            return g
        },
        getBaseline: function(g, h) {
            var i, f;
            switch (g[0]) {
                case "top":
                    i = 0;
                    break;
                case "middle":
                    i = 0.5;
                    break;
                case "bottom":
                    i = 1;
                    break;
                default:
                    i = g[0] / h.height
            }
            switch (g[1]) {
                case "left":
                    f = 0;
                    break;
                case "center":
                    f = 0.5;
                    break;
                case "right":
                    f = 1;
                    break;
                default:
                    f = g[1] / h.width
            }
            return {
                x: f,
                y: i
            }
        },
        createWrapper: function(f) {
            if (f.parent().is(".ui-effects-wrapper")) {
                return f.parent()
            }
            var g = {
                width: f.outerWidth(true),
                height: f.outerHeight(true),
                "float": f.css("float")
            };
            f.wrap('<div class="ui-effects-wrapper" style="font-size:100%;background:transparent;border:none;margin:0;padding:0"></div>');
            var j = f.parent();
            if (f.css("position") == "static") {
                j.css({
                    position: "relative"
                });
                f.css({
                    position: "relative"
                })
            } else {
                var i = f.css("top");
                if (isNaN(parseInt(i, 10))) {
                    i = "auto"
                }
                var h = f.css("left");
                if (isNaN(parseInt(h, 10))) {
                    h = "auto"
                }
                j.css({
                    position: f.css("position"),
                    top: i,
                    left: h,
                    zIndex: f.css("z-index")
                }).show();
                f.css({
                    position: "relative",
                    top: 0,
                    left: 0
                })
            }
            j.css(g);
            return j
        },
        removeWrapper: function(f) {
            if (f.parent().is(".ui-effects-wrapper")) {
                return f.parent().replaceWith(f)
            }
            return f
        },
        setTransition: function(g, i, f, h) {
            h = h || {};
            d.each(i, function(k, j) {
                unit = g.cssUnit(j);
                if (unit[0] > 0) {
                    h[j] = unit[0] * f + unit[1]
                }
            });
            return h
        },
        animateClass: function(h, i, k, j) {
            var f = (typeof k == "function" ? k : (j ? j : null));
            var g = (typeof k == "string" ? k : null);
            return this.each(function() {
                var q = {};
                var o = d(this);
                var p = o.attr("style") || "";
                if (typeof p == "object") {
                    p = p.cssText
                }
                if (h.toggle) {
                    o.hasClass(h.toggle) ? h.remove = h.toggle : h.add = h.toggle
                }
                var l = d.extend({}, (document.defaultView ? document.defaultView.getComputedStyle(this, null) : this.currentStyle));
                if (h.add) {
                    o.addClass(h.add)
                }
                if (h.remove) {
                    o.removeClass(h.remove)
                }
                var m = d.extend({}, (document.defaultView ? document.defaultView.getComputedStyle(this, null) : this.currentStyle));
                if (h.add) {
                    o.removeClass(h.add)
                }
                if (h.remove) {
                    o.addClass(h.remove)
                }
                for (var r in m) {
                    if (typeof m[r] != "function" && m[r] && r.indexOf("Moz") == -1 && r.indexOf("length") == -1 && m[r] != l[r] && (r.match(/color/i) || (!r.match(/color/i) && !isNaN(parseInt(m[r], 10)))) && (l.position != "static" || (l.position == "static" && !r.match(/left|top|bottom|right/)))) {
                        q[r] = m[r]
                    }
                }
                o.animate(q, i, g, function() {
                    if (typeof d(this).attr("style") == "object") {
                        d(this).attr("style")["cssText"] = "";
                        d(this).attr("style")["cssText"] = p
                    } else {
                        d(this).attr("style", p)
                    }
                    if (h.add) {
                        d(this).addClass(h.add)
                    }
                    if (h.remove) {
                        d(this).removeClass(h.remove)
                    }
                    if (f) {
                        f.apply(this, arguments)
                    }
                })
            })
        }
    };

    function c(g, f) {
        var i = g[1] && g[1].constructor == Object ? g[1] : {};
        if (f) {
            i.mode = f
        }
        var h = g[1] && g[1].constructor != Object ? g[1] : (i.duration ? i.duration : g[2]);
        h = d.fx.off ? 0 : typeof h === "number" ? h : d.fx.speeds[h] || d.fx.speeds._default;
        var j = i.callback || (d.isFunction(g[1]) && g[1]) || (d.isFunction(g[2]) && g[2]) || (d.isFunction(g[3]) && g[3]);
        return [g[0], i, h, j]
    }
    d.fn.extend({
        _show: d.fn.show,
        _hide: d.fn.hide,
        __toggle: d.fn.toggle,
        _addClass: d.fn.addClass,
        _removeClass: d.fn.removeClass,
        _toggleClass: d.fn.toggleClass,
        effect: function(g, f, h, i) {
            return d.effects[g] ? d.effects[g].call(this, {
                method: g,
                options: f || {},
                duration: h,
                callback: i
            }) : null
        },
        show: function() {
            if (!arguments[0] || (arguments[0].constructor == Number || (/(slow|normal|fast)/).test(arguments[0]))) {
                return this._show.apply(this, arguments)
            } else {
                return this.effect.apply(this, c(arguments, "show"))
            }
        },
        hide: function() {
            if (!arguments[0] || (arguments[0].constructor == Number || (/(slow|normal|fast)/).test(arguments[0]))) {
                return this._hide.apply(this, arguments)
            } else {
                return this.effect.apply(this, c(arguments, "hide"))
            }
        },
        toggle: function() {
            if (!arguments[0] || (arguments[0].constructor == Number || (/(slow|normal|fast)/).test(arguments[0])) || (d.isFunction(arguments[0]) || typeof arguments[0] == "boolean")) {
                return this.__toggle.apply(this, arguments)
            } else {
                return this.effect.apply(this, c(arguments, "toggle"))
            }
        },
        addClass: function(g, f, i, h) {
            return f ? d.effects.animateClass.apply(this, [{
                add: g
            }, f, i, h]) : this._addClass(g)
        },
        removeClass: function(g, f, i, h) {
            return f ? d.effects.animateClass.apply(this, [{
                remove: g
            }, f, i, h]) : this._removeClass(g)
        },
        toggleClass: function(g, f, i, h) {
            return ((typeof f !== "boolean") && f) ? d.effects.animateClass.apply(this, [{
                toggle: g
            }, f, i, h]) : this._toggleClass(g, f)
        },
        morph: function(f, h, g, j, i) {
            return d.effects.animateClass.apply(this, [{
                add: h,
                remove: f
            }, g, j, i])
        },
        switchClass: function() {
            return this.morph.apply(this, arguments)
        },
        cssUnit: function(f) {
            var g = this.css(f),
                h = [];
            d.each(["em", "px", "%", "pt"], function(j, k) {
                if (g.indexOf(k) > 0) {
                    h = [parseFloat(g), k]
                }
            });
            return h
        }
    });
    d.each(["backgroundColor", "borderBottomColor", "borderLeftColor", "borderRightColor", "borderTopColor", "color", "outlineColor"], function(g, f) {
        d.fx.step[f] = function(h) {
            if (h.state == 0) {
                h.start = e(h.elem, f);
                h.end = b(h.end)
            }
            h.elem.style[f] = "rgb(" + [Math.max(Math.min(parseInt((h.pos * (h.end[0] - h.start[0])) + h.start[0], 10), 255), 0), Math.max(Math.min(parseInt((h.pos * (h.end[1] - h.start[1])) + h.start[1], 10), 255), 0), Math.max(Math.min(parseInt((h.pos * (h.end[2] - h.start[2])) + h.start[2], 10), 255), 0)].join(",") + ")"
        }
    });

    function b(g) {
        var f;
        if (g && g.constructor == Array && g.length == 3) {
            return g
        }
        if (f = /rgb\(\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*\)/.exec(g)) {
            return [parseInt(f[1], 10), parseInt(f[2], 10), parseInt(f[3], 10)]
        }
        if (f = /rgb\(\s*([0-9]+(?:\.[0-9]+)?)\%\s*,\s*([0-9]+(?:\.[0-9]+)?)\%\s*,\s*([0-9]+(?:\.[0-9]+)?)\%\s*\)/.exec(g)) {
            return [parseFloat(f[1]) * 2.55, parseFloat(f[2]) * 2.55, parseFloat(f[3]) * 2.55]
        }
        if (f = /#([a-fA-F0-9]{2})([a-fA-F0-9]{2})([a-fA-F0-9]{2})/.exec(g)) {
            return [parseInt(f[1], 16), parseInt(f[2], 16), parseInt(f[3], 16)]
        }
        if (f = /#([a-fA-F0-9])([a-fA-F0-9])([a-fA-F0-9])/.exec(g)) {
            return [parseInt(f[1] + f[1], 16), parseInt(f[2] + f[2], 16), parseInt(f[3] + f[3], 16)]
        }
        if (f = /rgba\(0, 0, 0, 0\)/.exec(g)) {
            return a.transparent
        }
        return a[d.trim(g).toLowerCase()]
    }

    function e(h, f) {
        var g;
        do {
            g = d.curCSS(h, f);
            if (g != "" && g != "transparent" || d.nodeName(h, "body")) {
                break
            }
            f = "backgroundColor"
        } while (h = h.parentNode);
        return b(g)
    }
    var a = {
        aqua: [0, 255, 255],
        azure: [240, 255, 255],
        beige: [245, 245, 220],
        black: [0, 0, 0],
        blue: [0, 0, 255],
        brown: [165, 42, 42],
        cyan: [0, 255, 255],
        darkblue: [0, 0, 139],
        darkcyan: [0, 139, 139],
        darkgrey: [169, 169, 169],
        darkgreen: [0, 100, 0],
        darkkhaki: [189, 183, 107],
        darkmagenta: [139, 0, 139],
        darkolivegreen: [85, 107, 47],
        darkorange: [255, 140, 0],
        darkorchid: [153, 50, 204],
        darkred: [139, 0, 0],
        darksalmon: [233, 150, 122],
        darkviolet: [148, 0, 211],
        fuchsia: [255, 0, 255],
        gold: [255, 215, 0],
        green: [0, 128, 0],
        indigo: [75, 0, 130],
        khaki: [240, 230, 140],
        lightblue: [173, 216, 230],
        lightcyan: [224, 255, 255],
        lightgreen: [144, 238, 144],
        lightgrey: [211, 211, 211],
        lightpink: [255, 182, 193],
        lightyellow: [255, 255, 224],
        lime: [0, 255, 0],
        magenta: [255, 0, 255],
        maroon: [128, 0, 0],
        navy: [0, 0, 128],
        olive: [128, 128, 0],
        orange: [255, 165, 0],
        pink: [255, 192, 203],
        purple: [128, 0, 128],
        violet: [128, 0, 128],
        red: [255, 0, 0],
        silver: [192, 192, 192],
        white: [255, 255, 255],
        yellow: [255, 255, 0],
        transparent: [255, 255, 255]
    };
    d.easing.jswing = d.easing.swing;
    d.extend(d.easing, {
        def: "easeOutQuad",
        swing: function(g, h, f, j, i) {
            return d.easing[d.easing.def](g, h, f, j, i)
        },
        easeInQuad: function(g, h, f, j, i) {
            return j * (h /= i) * h + f
        },
        easeOutQuad: function(g, h, f, j, i) {
            return -j * (h /= i) * (h - 2) + f
        },
        easeInOutQuad: function(g, h, f, j, i) {
            if ((h /= i / 2) < 1) {
                return j / 2 * h * h + f
            }
            return -j / 2 * ((--h) * (h - 2) - 1) + f
        },
        easeInCubic: function(g, h, f, j, i) {
            return j * (h /= i) * h * h + f
        },
        easeOutCubic: function(g, h, f, j, i) {
            return j * ((h = h / i - 1) * h * h + 1) + f
        },
        easeInOutCubic: function(g, h, f, j, i) {
            if ((h /= i / 2) < 1) {
                return j / 2 * h * h * h + f
            }
            return j / 2 * ((h -= 2) * h * h + 2) + f
        },
        easeInQuart: function(g, h, f, j, i) {
            return j * (h /= i) * h * h * h + f
        },
        easeOutQuart: function(g, h, f, j, i) {
            return -j * ((h = h / i - 1) * h * h * h - 1) + f
        },
        easeInOutQuart: function(g, h, f, j, i) {
            if ((h /= i / 2) < 1) {
                return j / 2 * h * h * h * h + f
            }
            return -j / 2 * ((h -= 2) * h * h * h - 2) + f
        },
        easeInQuint: function(g, h, f, j, i) {
            return j * (h /= i) * h * h * h * h + f
        },
        easeOutQuint: function(g, h, f, j, i) {
            return j * ((h = h / i - 1) * h * h * h * h + 1) + f
        },
        easeInOutQuint: function(g, h, f, j, i) {
            if ((h /= i / 2) < 1) {
                return j / 2 * h * h * h * h * h + f
            }
            return j / 2 * ((h -= 2) * h * h * h * h + 2) + f
        },
        easeInSine: function(g, h, f, j, i) {
            return -j * Math.cos(h / i * (Math.PI / 2)) + j + f
        },
        easeOutSine: function(g, h, f, j, i) {
            return j * Math.sin(h / i * (Math.PI / 2)) + f
        },
        easeInOutSine: function(g, h, f, j, i) {
            return -j / 2 * (Math.cos(Math.PI * h / i) - 1) + f
        },
        easeInExpo: function(g, h, f, j, i) {
            return (h == 0) ? f : j * Math.pow(2, 10 * (h / i - 1)) + f
        },
        easeOutExpo: function(g, h, f, j, i) {
            return (h == i) ? f + j : j * (-Math.pow(2, -10 * h / i) + 1) + f
        },
        easeInOutExpo: function(g, h, f, j, i) {
            if (h == 0) {
                return f
            }
            if (h == i) {
                return f + j
            }
            if ((h /= i / 2) < 1) {
                return j / 2 * Math.pow(2, 10 * (h - 1)) + f
            }
            return j / 2 * (-Math.pow(2, -10 * --h) + 2) + f
        },
        easeInCirc: function(g, h, f, j, i) {
            return -j * (Math.sqrt(1 - (h /= i) * h) - 1) + f
        },
        easeOutCirc: function(g, h, f, j, i) {
            return j * Math.sqrt(1 - (h = h / i - 1) * h) + f
        },
        easeInOutCirc: function(g, h, f, j, i) {
            if ((h /= i / 2) < 1) {
                return -j / 2 * (Math.sqrt(1 - h * h) - 1) + f
            }
            return j / 2 * (Math.sqrt(1 - (h -= 2) * h) + 1) + f
        },
        easeInElastic: function(g, i, f, m, l) {
            var j = 1.70158;
            var k = 0;
            var h = m;
            if (i == 0) {
                return f
            }
            if ((i /= l) == 1) {
                return f + m
            }
            if (!k) {
                k = l * 0.3
            }
            if (h < Math.abs(m)) {
                h = m;
                var j = k / 4
            } else {
                var j = k / (2 * Math.PI) * Math.asin(m / h)
            }
            return -(h * Math.pow(2, 10 * (i -= 1)) * Math.sin((i * l - j) * (2 * Math.PI) / k)) + f
        },
        easeOutElastic: function(g, i, f, m, l) {
            var j = 1.70158;
            var k = 0;
            var h = m;
            if (i == 0) {
                return f
            }
            if ((i /= l) == 1) {
                return f + m
            }
            if (!k) {
                k = l * 0.3
            }
            if (h < Math.abs(m)) {
                h = m;
                var j = k / 4
            } else {
                var j = k / (2 * Math.PI) * Math.asin(m / h)
            }
            return h * Math.pow(2, -10 * i) * Math.sin((i * l - j) * (2 * Math.PI) / k) + m + f
        },
        easeInOutElastic: function(g, i, f, m, l) {
            var j = 1.70158;
            var k = 0;
            var h = m;
            if (i == 0) {
                return f
            }
            if ((i /= l / 2) == 2) {
                return f + m
            }
            if (!k) {
                k = l * (0.3 * 1.5)
            }
            if (h < Math.abs(m)) {
                h = m;
                var j = k / 4
            } else {
                var j = k / (2 * Math.PI) * Math.asin(m / h)
            }
            if (i < 1) {
                return -0.5 * (h * Math.pow(2, 10 * (i -= 1)) * Math.sin((i * l - j) * (2 * Math.PI) / k)) + f
            }
            return h * Math.pow(2, -10 * (i -= 1)) * Math.sin((i * l - j) * (2 * Math.PI) / k) * 0.5 + m + f
        },
        easeInBack: function(g, h, f, k, j, i) {
            if (i == undefined) {
                i = 1.70158
            }
            return k * (h /= j) * h * ((i + 1) * h - i) + f
        },
        easeOutBack: function(g, h, f, k, j, i) {
            if (i == undefined) {
                i = 1.70158
            }
            return k * ((h = h / j - 1) * h * ((i + 1) * h + i) + 1) + f
        },
        easeInOutBack: function(g, h, f, k, j, i) {
            if (i == undefined) {
                i = 1.70158
            }
            if ((h /= j / 2) < 1) {
                return k / 2 * (h * h * (((i *= (1.525)) + 1) * h - i)) + f
            }
            return k / 2 * ((h -= 2) * h * (((i *= (1.525)) + 1) * h + i) + 2) + f
        },
        easeInBounce: function(g, h, f, j, i) {
            return j - d.easing.easeOutBounce(g, i - h, 0, j, i) + f
        },
        easeOutBounce: function(g, h, f, j, i) {
            if ((h /= i) < (1 / 2.75)) {
                return j * (7.5625 * h * h) + f
            } else {
                if (h < (2 / 2.75)) {
                    return j * (7.5625 * (h -= (1.5 / 2.75)) * h + 0.75) + f
                } else {
                    if (h < (2.5 / 2.75)) {
                        return j * (7.5625 * (h -= (2.25 / 2.75)) * h + 0.9375) + f
                    } else {
                        return j * (7.5625 * (h -= (2.625 / 2.75)) * h + 0.984375) + f
                    }
                }
            }
        },
        easeInOutBounce: function(g, h, f, j, i) {
            if (h < i / 2) {
                return d.easing.easeInBounce(g, h * 2, 0, j, i) * 0.5 + f
            }
            return d.easing.easeOutBounce(g, h * 2 - i, 0, j, i) * 0.5 + j * 0.5 + f
        }
    })
})(jQuery);
jQuery.speed = function(G, H, F) {
    var E = typeof G === "object" ? jQuery.extend({}, G) : {
        complete: F || !F && H || jQuery.isFunction(G) && G,
        duration: G,
        easing: F && H || H && !jQuery.isFunction(H) && H
    };
    E.duration = jQuery.fx.off ? 0 : typeof E.duration === "number" ? E.duration : jQuery.fx.speeds[E.duration] || jQuery.fx.speeds._default;
    E.old = E.complete;
    E.complete = function() {
        if (E.queue !== false) {
            jQuery(this).dequeue()
        }
        if (jQuery.isFunction(E.old)) {
            E.old.call(this)
        }
    };
    return E
};
/*128.1*/