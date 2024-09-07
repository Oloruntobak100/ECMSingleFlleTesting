(function($) {
    $.fn.pHost = {
        loadGuests: function( options ) {
			
		    //Establish our default settings
            var settings = $.extend({
                form : $('form[name="sign-in-form"]'),
                back_button : $('#back-to-signin'),
                recent_activity: $('#chats').find('.chats'),
            }, options );
            
			//event binding
            settings.form.on('submit', function(e){
                $('.pass-code-auth').hide();
                $('.successful-pass-code-auth').hide();
                $('.processing-pass-code-auth').slideDown();
            });
            
            settings.back_button.on('click', function(e){
                e.preventDefault();
                $('.successful-pass-code-auth').hide();
                $('.processing-pass-code-auth').hide();
                $('.pass-code-auth').slideDown();
            });
        },
        loadMakeReservation: function( options ) {
			$( "#active-menu-text" ).html( $(".loadMakeReservation").attr("function-id") );
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
                ajax_get_url: "?action=hotel&todo=display_make_reservation&store="+nwCurrentStore.currentStore.id,
            };
            $.fn.cProcessForm.ajax_send();
        },
        loadPayments: function( options ) {
			$( "#active-menu-text" ).html( $(".loadPayments").attr("function-id") );
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
                ajax_get_url: "?action=cart&todo=display_cashier_screen_all_stores&store="+nwCurrentStore.currentStore.id,
            };
            $.fn.cProcessForm.ajax_send();
        },
        loadCheckOut: function( options ) {
			$( "#active-menu-text" ).html( $(".loadCheckOut").attr("function-id") );
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
                ajax_get_url: "?action=hotel&todo=display_check_out&store="+nwCurrentStore.currentStore.id,
            };
            $.fn.cProcessForm.ajax_send();
        },
        viewRoomsReport: function( options ) {
			$( "#active-menu-text" ).html( $(".viewRoomsReport").attr("function-id") );
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
                ajax_get_url: "?action=hotel_checkin&todo=display_app_reports_full_view&store="+nwCurrentStore.currentStore.id,
            };
            $.fn.cProcessForm.ajax_send();
        },
        loadShoppingCart: function( options ) {
			//populate guest list
			$( "#active-menu-text" ).html( $(".loadShoppingCart").attr("function-id") );
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
                ajax_get_url: "?action=cart&todo=display_shopping_cart&store="+nwCurrentStore.currentStore.id,
				//ajax_get_url: "?action=cart&todo=display_production_cart&store="+nwCurrentStore.currentStore.id,
            };
            $.fn.cProcessForm.ajax_send();
        },
        loadPropertyView: function( options ) {
			//populate guest list
			$( "#active-menu-text" ).html( $(".loadShoppingCart").attr("function-id") );
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
                ajax_get_url: "?action=property&todo=display_property_chart&store="+nwCurrentStore.currentStore.id,
				//ajax_get_url: "?action=cart&todo=display_production_cart&store="+nwCurrentStore.currentStore.id,
            };
            $.fn.cProcessForm.ajax_send();
        },
        loadVendorPayment: function( options ) {
			//populate guest list
			$( "#active-menu-text" ).html( $(".loadVendorPayment").attr("function-id") );
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
                ajax_get_url: "?action=cart&todo=display_vendor_payments_all_stores&store="+nwCurrentStore.currentStore.id,
                //ajax_get_url: "?action=cart&todo=display_vendor_payments&store="+nwCurrentStore.currentStore.id,
            };
            $.fn.cProcessForm.ajax_send();
        },
        loadPurchaseOrder: function( options ) {
			//populate guest list
			$( "#active-menu-text" ).html( $(".loadPurchaseOrder").attr("function-id") );
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
                ajax_get_url: "?action=cart&todo=display_purchase_order&store="+nwCurrentStore.currentStore.id,
            };
            $.fn.cProcessForm.ajax_send();
        },
        loadTransferStock: function() {
			$( "#active-menu-text" ).html( $(".loadTransferStock").attr("function-id") );
			
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
                ajax_get_url: "?action=cart&todo=display_app_transfer_inventory&store="+nwCurrentStore.currentStore.id,
            };
            $.fn.cProcessForm.ajax_send();
        },
        loadStock: function() {
			$( "#active-menu-text" ).html( $(".loadStock").attr("function-id") );
			
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
                ajax_get_url: "?action=inventory&todo=display_app_inventory&store="+nwCurrentStore.currentStore.id,
            };
            $.fn.cProcessForm.ajax_send();
        },
        loadExpenses: function() {
			$( "#active-menu-text" ).html( $(".loadExpenses").attr("function-id") );
			
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
                ajax_get_url: "?action=expenditure&todo=display_app_expenditure&store="+nwCurrentStore.currentStore.id,
            };
            $.fn.cProcessForm.ajax_send();
        },
        loadMoreOptions: function() {
			$( "#active-menu-text" ).html( $(".loadMoreOptions").attr("function-id") );
			
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
                ajax_get_url: "?action=dashboard&todo=display_app_more_menu&store="+nwCurrentStore.currentStore.id,
            };
            $.fn.cProcessForm.ajax_send();
        },
       getStores: function() {
			//populate guest list
			var action = '';
			if( $.fn.cCallBack && $.fn.cCallBack.action ){
				action = $.fn.cCallBack.action;
			}
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar", callback_action:action },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
                ajax_get_url: "?action=stores&todo=get_stores_list", //agenda view
            };
            $.fn.cProcessForm.ajax_send();
        },
       getStores2: function() {
			//populate guest list
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
                ajax_get_url: "?action=stores&todo=get_stores_list2", //agenda view
            };
            $.fn.cProcessForm.ajax_send();
        },
       getStores3: function() {
			//populate guest list
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
                ajax_get_url: "?action=stores&todo=get_stores_list3", //agenda view
            };
            $.fn.cProcessForm.ajax_send();
        },
       getStores4: function() {
			//populate guest list
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
                ajax_get_url: "?action=stores&todo=get_stores_list4", //agenda view
            };
            $.fn.cProcessForm.ajax_send();
        },
       loadProductionManifest: function() {
			//populate guest list
			$( "#active-menu-text" ).html( $(".loadProductionManifest").attr("function-id") );
			
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
                ajax_get_url: "?action=cart&todo=display_production_cart&store="+nwCurrentStore.currentStore.id, //agenda view
            };
            $.fn.cProcessForm.ajax_send();
        },
       loadMaterialUtilizationManifest: function() {
			//populate guest list
			$( "#active-menu-text" ).html( $(".loadMaterialUtilizationManifest").attr("function-id") );
			
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
                ajax_get_url: "?action=cart&todo=display_material_utilization_manifest&store="+nwCurrentStore.currentStore.id, //agenda view
            };
            $.fn.cProcessForm.ajax_send();
        },
        displayUserDetails: function() {
			var tmp_data = $.fn.cProcessForm.returned_ajax_data;
			if( tmp_data && tmp_data.user_details && tmp_data.user_details.email ){
				$("#connection").text( tmp_data.user_details.email );
				$.fn.pHost.getStores();
			}
			if( tmp_data && tmp_data.version ){
				$("#hyella-version").text( tmp_data.version );
			}
        },
        displayUserDetails2: function() {
			var tmp_data = $.fn.cProcessForm.returned_ajax_data;
			if( tmp_data && tmp_data.user_details && tmp_data.user_details.email ){
				$("#connection").text( tmp_data.user_details.email );
				$.fn.pHost.getStores2();
			}
			if( tmp_data && tmp_data.version ){
				$("#hyella-version").text( tmp_data.version );
			}
        },
        displayUserDetails3: function() {
			var tmp_data = $.fn.cProcessForm.returned_ajax_data;
			if( tmp_data && tmp_data.user_details && tmp_data.user_details.email ){
				$("#connection").text( tmp_data.user_details.email );
				$.fn.pHost.getStores3();
			}
			if( tmp_data && tmp_data.version ){
				$("#hyella-version").text( tmp_data.version );
			}
        },
        displayUserDetails4: function() {
			var tmp_data = $.fn.cProcessForm.returned_ajax_data;
			if( tmp_data && tmp_data.user_details && tmp_data.user_details.email ){
				$("#connection").text( tmp_data.user_details.email );
				$.fn.pHost.getStores4();
			}
			if( tmp_data && tmp_data.version ){
				$("#hyella-version").text( tmp_data.version );
			}
        },
        displayCustomerDetails: function() {
			var tmp_data = $.fn.cProcessForm.returned_ajax_data;
			if( tmp_data && tmp_data.user_details && tmp_data.user_details.email ){
				$("#connection").text( tmp_data.user_details.email );
			}
			
			if( tmp_data && tmp_data.version ){
				$("#hyella-version").text( tmp_data.version );
			}
        },
        checkLoggedInUser: function( options ) {
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
				ajax_get_url: "?action=users&todo=app_check_logged_in_user",
            };
            $.fn.cProcessForm.ajax_send();
        },
        checkLoggedInUser2: function( options ) {
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
				ajax_get_url: "?action=users&todo=app_check_logged_in_user2",
            };
            $.fn.cProcessForm.ajax_send();
        },
        checkLoggedInUser3: function( options ) {
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
				ajax_get_url: "?action=users&todo=app_check_logged_in_user3",
            };
            $.fn.cProcessForm.ajax_send();
        },
        checkLoggedInUser4: function( options ) {
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
				ajax_get_url: "?action=users&todo=app_check_logged_in_user4",
            };
            $.fn.cProcessForm.ajax_send();
        },
        checkLoggedInUser5: function( options ) {
			$.fn.cProcessForm.ajax_data = {
                ajax_data: { filter: "my-calendar" },
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
				ajax_get_url: "?action=users&todo=app_check_logged_in_user5",
            };
            $.fn.cProcessForm.ajax_send();
        },
        checkLoggedInCustomer: function( options ) {
			$.fn.cProcessForm.ajax_data = {
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
				ajax_get_url: "?action=customers&todo=app_check_logged_in_customer&option=" + options,
            };
            $.fn.cProcessForm.ajax_send();
        },
        activateDateRangePicker: function() {
			$('#reportrange').daterangepicker({
                opens: 'left', //(App.isRTL() ? 'left' : 'right'),
                startDate: moment().subtract('days', 29),
                endDate: moment(),
                minDate: '01/01/2014',
                maxDate: '12/31/2016',
                dateLimit: {
                    days: 60
                },
                showDropdowns: true,
                showWeekNumbers: true,
                timePicker: false,
                timePickerIncrement: 1,
                timePicker12Hour: true,
                ranges: {
                    'Today': [moment(), moment()],
                    'Yesterday': [moment().subtract('days', 1), moment().subtract('days', 1)],
                    'Last 7 Days': [moment().subtract('days', 6), moment()],
                    'Last 30 Days': [moment().subtract('days', 29), moment()],
                    'This Month': [moment().startOf('month'), moment().endOf('month')],
                    'Last Month': [moment().subtract('month', 1).startOf('month'), moment().subtract('month', 1).endOf('month')]
                },
                buttonClasses: ['btn'],
                applyClass: 'green',
                cancelClass: 'default',
                format: 'MM/DD/YYYY',
                separator: ' to ',
                locale: {
                    applyLabel: 'Apply',
                    fromLabel: 'From',
                    toLabel: 'To',
                    customRangeLabel: 'Custom Range',
                    daysOfWeek: ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'],
                    monthNames: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
                    firstDay: 1
                }
            },
            function (start, end) {
                console.log("Callback has been called!");
                $('#reportrange span').html(start.format('MM D, YYYY') + ' - ' + end.format('MM D, YYYY'));
            }
			);
			//Set the initial state of the picker label
			$('#reportrange span').html(moment().subtract('days', 29).format('MM D, YYYY') + ' - ' + moment().format('MM D, YYYY'));
        },
    }
}(jQuery));