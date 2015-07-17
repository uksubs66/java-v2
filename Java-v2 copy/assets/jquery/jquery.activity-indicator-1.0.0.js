/*!
 * NETEYE Activity Indicator jQuery Plugin
 *
 * Copyright (c) 2010 NETEYE GmbH
 * Licensed under the MIT license
 *
 * Author: Felix Gnass [fgnass at neteye dot de]
 * Version: 1.0.0
 */
 
/**
 * Plugin that renders a customisable activity indicator (spinner) using SVG or VML.
 */
(function($) {

	$.fn.activity = function(opts) {
		$(this).each(function() {
			var $this = $(this);
			var el = $this.data('activity');
			if (el) {
				el.remove();
				$this.removeData('activity');
			}
                        if((typeof(opts) == "string") && opts == "stop"){
                            // Do nothing
                        } else if (opts !== false) {
				//opts = $.extend({color: $this.css('color')}, $.fn.activity.defaults, opts);
                                
				opts = $.extend({color: $this.css('white')}, $.fn.activity.defaults, opts);
				el = render($this, opts).css('position', 'absolute').prependTo(opts.outside ? 'body' : $this);
                                if(opts['class']){
                                    el.addClass(opts['class']);
                                    //console.log('addclass:'+opts['class']);
                                }
				var h = $this.outerHeight() - el.outerHeight();
				var w = $this.outerWidth() - el.outerWidth();
				var margin = {
					top: opts.valign == 'top' ? opts.padding : opts.valign == 'bottom' ? h - opts.padding : Math.floor(h / 2),
					left: opts.align == 'left' ? opts.padding : opts.align == 'right' ? w - opts.padding : Math.floor(w / 2)
				};
				var offset = $this.offset();
				if (opts.outside) {
					el.css({top: offset.top + 'px', left: offset.left + 'px'});
				}
				else {
					margin.top -= el.offset().top - offset.top;
					margin.left -= el.offset().left - offset.left;
				}
				el.css({marginTop: margin.top + 'px', marginLeft: margin.left + 'px'});
				$this.data('activity', el);
			}
		});
		return this;
	};
	
	$.fn.activity.defaults = {
		segments: 12,
		space: 3,
		length: 7,
		width: 4,
		speed: 1.2,
		align: 'center',
		valign: 'center',
		padding: 4,
                'class': 'ajax_loader'
	};
	
	$.fn.activity.getOpacity = function(opts, i) {
		var steps = opts.steps || opts.segments-1;
		var end = opts.opacity !== undefined ? opts.opacity : 1/steps;
		return 1 - Math.min(i, steps) * (1 - end) / steps;
	};
	
	/**
	 * Default rendering strategy. If neither SVG nor VML is available, a div with class-name 'busy' 
	 * is inserted, that can be styled with CSS to display an animated gif as fallback.
	 */
	var render = function() {
		return $('<div><img style="width:32px;height:32px;border:none;display:block;margin:0;padding:0;" src="../assets/images/ajax-loader.gif" /></div>').addClass('busy');
	};
	

})(jQuery);
