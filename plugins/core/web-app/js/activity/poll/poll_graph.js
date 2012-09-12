var PollGraph = function(pollResponse, ownerId, statsUrl) {
	var ___start___,
	_loaded = false,
	_refresh = false,
	_stats = {},
	_getStats = function() {
		if(jQuery.isEmptyObject(_stats)) {
			_stats.xdata = $.map(pollResponse, function(a) { return a.value; });
			_stats.data =  $.map(pollResponse, function(a) { return a.percent; });
		}
		return _stats;
	},
	_updateStats = function() {
		var params = { ownerId: ownerId };
		$.getJSON(statsUrl, params, function(data) {
			_refresh = true;
			_stats.id = $.map(data, function(a) { return a.id; });
			_stats.count = $.map(data, function(a) { return a.count; });
			_stats.xdata = $.map(data, function(a) { return a.value; });
			_stats.data =  $.map(data, function(a) { return a.percent; });
			$.each(_stats.id, function(index, value) {
				$("#response-"+ value).find("td.count").html(_stats.count[index]);
				$("#response-"+ value).find("td.percent").html(_stats.data[index] + "%");
			});
			_showGraph();
		});
	},
	_hideMessages = function() {
		_loaded = true;
		var pollGraphBtn = $("#poll-graph-btn");
		pollGraphBtn.html(i18n("fmessage.hidepolldetails"));
		pollGraphBtn.addClass("hide-arrow");
		pollGraphBtn.removeClass("show-arrow");
		$("#poll-details").show();
		$('#main-list').hide();
		$("#main-list-foot").hide();
	},
	_showMessages = function() {
		var pollGraphBtn = $("#poll-graph-btn");
		$("#poll-details").hide();
		pollGraphBtn.html(i18n("fmessage.showpolldetails"));
		pollGraphBtn.addClass("show-arrow");
		pollGraphBtn.removeClass("hide-arrow");
		$('#main-list').show();
		$("#main-list-foot").show();
		_loaded = false;
	},
	_setDimensions = function(pollGraph, top, left, width) {
		pollGraph.css('top', top);
		pollGraph.css('left', left);
		pollGraph.css('width', width);
	},
	_showGraph = function() {
		var mainListHead = $("#main-list-head");
		var top = mainListHead.offset().top + mainListHead.height();
		var left = mainListHead.offset().left;
		var width = mainListHead.outerWidth();
		var pollGraph = $("#pollGraph");
		_setDimensions($("#pollGraph-container"), top, left, width);
		_setDimensions(pollGraph, top, left, width - 30);
		var show = true;
		if (_loaded || _refresh) {
			var stats = _getStats();
			pollGraph.empty();
			_refresh = false;
			plot3 = $.jqplot("pollGraph", [stats.data], {
					seriesColors: ["#40B857", "#F2202B", "#ff9600", "#D5AFC6", "#9E9E9E","#DFDFDF,"],
					captureRightClick: true,
					seriesDefaults:{
							renderer:$.jqplot.BarRenderer,
							rendererOptions:{ barMargin:15, varyBarColor:true, highlightMouseDown:true },
							pointLabels:{ show: true } },
					axes: {
							xaxis:{ renderer:$.jqplot.CategoryAxisRenderer, ticks:stats.xdata },
							yaxis:{ ticks:[0, 100], tickOptions:{formatString:'%d\%'} }
					}
			});
		}					
	},
	_show = function() {
		if(_loaded) {
			_showMessages();
		} else {
			_hideMessages();
			_showGraph();
		}
	},
	___end___;
	return {
		updateStats:_updateStats,
		show:_show
	};
}

