var nwChartJs = {
	data:{},
	init:function(){},
	loadChartData:function( e = {} ){
		if( e.html_container &&  e.data && e.data.type && e.data.data && e.data.data.labels && e.data.data.datasets ){
			var ctx = $('#'+e.html_container);
			var myChart = new Chart(ctx, e.data);
		}
	},
	loadSingleChart:function(){
		if( $.fn.cProcessForm.returned_ajax_data && $.fn.cProcessForm.returned_ajax_data.data && $.fn.cProcessForm.returned_ajax_data.html_replacement_selector ){
			var h =$.fn.cProcessForm.returned_ajax_data.html_replacement_selector;
			var d =$.fn.cProcessForm.returned_ajax_data.data;

			var width = $( '#' + h ).attr( 'width' );
			var height = $( '#' + h ).attr( 'height' );

			$( '#' + h + '-container' ).html( '<canvas id="'+ h +'" width="'+ width +'" height="'+ height +'"></canvas>' );
			
			var ctx = $('#'+h);
			var myChart = new Chart(ctx, d);
		}
	},
	closeModal: function(){
		$( 'button#modal-popup-close' ).click();
	},
};
nwChartJs.init();
