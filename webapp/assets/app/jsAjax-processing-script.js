(function($) {
    $nwProcessor = {
		current_module: '1357383943_1',
		current_record: '',
		//Store HTML ID of Last Clicked Function
		clicked_menu:'',
		//Store HTML ID of Last Clicked Popup Function
		clicked_main_menu:'',
		//Name of Table to Search
		search_table:'',
		//Name of Table to Toggle its Columns
		column_toggle_table:'',
		//Number of Column to toggle
		column_toggle_num:'',
		//Name of Column to toggle
		column_toggle_name:'',
		//Selected Record ID
		single_selected_record:'',
		//Selected Records IDs
		multiple_selected_record_id:'',
		//Selected Records Details
		details_of_multiple_selected_records:'',
		class_action:'',
		class_name:'',
		module_id:'',
		pagepointer:$('#pagepointer').text(),
		clicked_action_button:'',
		confirm_action_prompt:1,
		form_method:'get',
		ajax_data_type:'json',
		ajax_data:'',
		ajax_get_url:'',
		ajax_action:'',
		ajax_container:'',
		ajax_notice_container:'',
		//AJAX Request Data Before Sending
		ajax_request_data_before_sending_to_server:'',
		function_click_process:1,
		cancel_ajax_recursive_function:false,
		oTable:'',
		oNormalTable:'',
		//Last Position of Mouse on mouseup event
		last_position_of_mouse_on_mouse_up_x:0,
		last_position_of_mouse_on_mouse_up_y:0,
		//Variable to determine if entity is being renamed
		renaming_entity_in_progress:0,
		//Currently Opened Label
		currently_opened_label_id_in_report_letters_memo:'',
		editted_entity_source:'',
		//Determine if Menus have been bound to actions after initialization
		bound_menu_items_to_actions:0,
		test_view_entity:0,
		//Variable that determines the number of times notifications have to be closed prior to status update
		update_notifications_to_read:0,
		//Variable that determines whether to archive dataset
		archive_dataset:0,
		//Variable that determines the currently edited textarea
		editing_textarea:'',
		//Variable that determines the currently view port opened in reports , letters & memo
		report_letters_memo_current_view:'',
	
	//Request Modules
	generate_modules: function(){
		
		$.fn.cProcessForm.ajax_data = {
			ajax_data: {action:'modules', todo:'display'},
			form_method: 'get',
			ajax_data_type: 'json',
			ajax_action: 'generate_modules',
			ajax_container: '',
			ajax_get_url: "",
		};
		$.fn.cProcessForm.ajax_send();

	},
	populate_dashbaord: function(){
		
		$.fn.cProcessForm.ajax_data = {
			ajax_data: {action:'dashboard', todo:'populate_dashboard'},
			form_method: 'get',
			ajax_data_type: 'json',
			ajax_action: 'request_function_output',
			ajax_container: '',
			ajax_get_url: "",
		};
		$.fn.cProcessForm.ajax_send();

	},
	
	
	get_properties_of_school: function(){
		/*
		var t = '';
        if( $('#rollover-for-auth') && $('#rollover-for-auth').val() ){
            t = $('#rollover-for-auth').val();
        }
        
        if( $('#update-app') && $('#update-app').val() ){
			$nwProcessor.set_function_click_event();
            return false;
        }
		
		$.fn.cProcessForm.ajax_data = {
			ajax_data: {action:'appsettings', todo:'get_appsettings', tt:t },
			form_method: 'get',
			ajax_data_type: 'json',
			ajax_action: 'request_function_output',
			ajax_container: '',
			ajax_get_url: "",
		};
		$.fn.cProcessForm.ajax_send();
		*/
	},
	
	//FLAG Records
	flag_certain_records: function(){
		var $rows = $('#example').find('tr');
		
		$rows.each( function(){
			var flag = $(this).find('span.flag:first').attr('flag');
			var flags = $(this).find('span.flag').length;
			
			if( flags > 1 ){
				$(this).addClass( flag );
			}
		});
		
	},
	
	//Rehighlight Selected Record
	rehighlight_selected_record_function: function( $context ){
		
		if( $nwProcessor.selection ){
			var $jx2 = $nwProcessor.get_selection();
			if( ! $.isEmptyObject( $jx2 ) ){
				var $element = $( "#" + $nwProcessor.getDataTableID( 1 ) );
				$('input#datatable-select-all-checkbox').prop("checked", false );
				
				$element
				.find('tr:visible')
				.each(function(){
					var id_of_record = $(this).find('.datatables-record-id').attr('id');
					if( id_of_record && $jx2[ id_of_record ] ){
						
						$(this).addClass('row_selected');
					}
				});
			}
		}else{
  
			if( $nwProcessor.single_selected_record && $('#'+ $nwProcessor.single_selected_record) ){
   
						
	
												 
	
							   
									   
										   
	
							 
				var $selected_record = $('#'+ $nwProcessor.single_selected_record).parents('tr');
				
				if($selected_record){
					
					$selected_record.removeClass('row_selected');
					
					//Select Record Click Event
					$clicked_element_parent = $context;
					$clicked_element_group_selector = 'tr';
					
					var shiftctrlKey = false;
					$nwProcessor.select_record_click_event($clicked_element_parent, $selected_record , $clicked_element_group_selector, shiftctrlKey);
					
				}
			}
		}
	},
	
	//Quick Edit Form
	activate_quick_edit_form_submit: function(){
		
		$nwProcessor.activate_tooltip_for_form_element( $('#form-content-area').find('form').not('.ajax-activated') );
		$nwProcessor.activate_validation_for_required_form_element( $('#form-content-area').find('form').not('.ajax-activated') );
		
		//Bind Form Submit Event
		$('form.quick-edit-form')
		.not('.ajax-activated')
		.bind('submit', function( e ){
			e.preventDefault();
			
			$('form.quick-edit-form').data('pending', 0 );
			$nwProcessor.submit_form_data( $(this) );	
		})
		.addClass("ajax-activated");
		
		//Activate Ajax file upload
		$.fn.cProcessForm.ajaxFileUploader();
		//createUploader();
		
		//Bind Form Change Event
		$('form.quick-edit-form')
		.find('.form-gen-element')
								
															 
   
														  
										
   
														 
   
								   
						
					  
																								 
														  
		  
	 
	
														  
	
   
																
   
							   
												 

																												   
																										  

															
													

										
														 
							  
											  
							
	 

												 
	
		.bind('keyup', function(e){ 
			
			switch(e.keyCode){
			case 13:	//Enter key
			case 35:	//End key
			case 36:	//Home key
			case 37:	//Left arrow
			case 38:	//Up arrow
			case 39:	//Right arrow
			case 40:	//Down arrow
			case 34:	//Page down button
			case 33:	//Page up button
			case 65:	//Enter Key
			break;
			default:
				$(this).change();
			break;				
			}
		});
		
	},
	
	//Form Submission
	record_click_by_user: 0,
	select_record_click_function: function( $context ){
		if( ! $context ){
			var data_table_id = $nwProcessor.getDataTableID( 1 );
			$context = $('#'+data_table_id);
		}
		
		$context.find('tr').off('click');
		$context.find('tr .datatables-child-click').off('click');
		//$context.find('tr td').off('dblclick');
		
		//Bind Row Click Event
		$context
		.find('tr .datatables-child-click')
		.on('click',function(e){
			$.fn.cProcessForm.buttonClickRequest( $(this), e );
			e.stopPropagation();
		});
		
		$context
		.find('tr .datatables-stop-propagation')
		.on('click',function(e){
			e.stopPropagation();
		});
		
		$context
		.find('tr')
		.on('click',function(e){
			
			//Select Record Click Event
			$clicked_element_parent = $(this).parents('table');
			$clicked_element_group_selector = 'tr';
			
			if( $(this).hasClass("line-items-total-row") || $(this).hasClass("line-items-space-row") ){
				return false;
			}
			
			var shiftctrlKey = false;
			if( e.ctrlKey || e.shiftKey )
				shiftctrlKey = true;
			
			$nwProcessor.record_click_by_user = 1;
			
			
			$nwProcessor.select_record_click_event( $clicked_element_parent, $(this) , $clicked_element_group_selector, shiftctrlKey );
			$nwProcessor.record_click_by_user = 0;
			
			if( $nwProcessor.single_selected_record && $('form.quick-edit-form') && $('form.quick-edit-form').is(':visible') ){
				if( $('form.quick-edit-form').data('pending') )$('form.quick-edit-form').submit();
				
				$nwProcessor.populate_form_with_datatable_values( $('form.quick-edit-form'), $(this).find('.datatables-cell-id:eq(2)').parent('td') );
				
				$('form.quick-edit-form')
				.find('.form-gen-element:focus')
				.focus();
			}
			
			if( $nwProcessor.single_selected_record ){
				//scroll_to_top_of_selected_record();
			}
		});
		
		/*
		$context
		.find('tr')
		.find('td')
		.on('dblclick', function(e){
			//check if edit record button is visible
			if( $('a#edit-selected-record') && $('a#edit-selected-record').is(':visible') ){
				$clicked_element_parent = $(this).parents('table');
				$clicked_element_group_selector = 'tr';
			
				var shiftctrlKey = false;
				
				select_record_click_event($clicked_element_parent, $(this).parents('tr') , $clicked_element_group_selector, shiftctrlKey);
				
				$form
				.find('.form-gen-element')
				.attr('disabled', true);
				
				populate_form_with_datatable_values( $('#inline-edit-form-wrapper'), $(this) );
			}
		});*/
		
		$context
		.parents('.dataTables_scrollBody')
		.bind('scroll' , function(){
			var scrollpos = $(this).scrollLeft();
			
			var scrollwidth_lower = $(this).width();
			var scrollwidth_upper = $(this).siblings('.dataTables_scrollHead').find("table.display").width();
			
			var scrollwidth = scrollwidth_upper - scrollwidth_lower;
			
			if( scrollpos > scrollwidth ){
				$(this)
				.scrollLeft( scrollwidth );
			}
		});
		
	},
	
	populate_form_with_datatable_values: function( $form, $me ){
		
	},
	
	months: {
		"01":"Jan",
		"02":"Feb",
		"03":"Mar",
		"04":"Apr",
		"05":"May",
		"06":"Jun",
		"07":"Jul",
		"08":"Aug",
		"09":"Sep",
		"10":"Oct",
		"11":"Nov",
		"12":"Dec",
	},
	
	months_reverse: {
		"Jan":"01",
		"Feb":"02",
		"Mar":"03",
		"Apr":"04",
		"May":"05",
		"Jun":"06",
		"Jul":"07",
		"Aug":"08",
		"Sep":"09",
		"Oct":"10",
		"Nov":"11",
		"Dec":"12",
	},
	
	get_selection: function(){
		if( $nwProcessor.selection ){
			var $jx = $( $nwProcessor.selection ).find("textarea[name='data']").val();
			var $jx2 = {};
			if( $jx ){
				$jx2 = JSON.parse( $jx );
				if( ! $jx2 ){
					$jx2 = {};
				}
			}
			return $jx2;
		}
		return {};
	},
	set_selection: function( $jx2 ){
		if( $nwProcessor.selection ){
			var $jt = '';
			var len = 0;
			if( $jx2 && ! $.isEmptyObject( $jx2 ) ){
				$jt = JSON.stringify( $jx2 );
				//len = $jt.split(',').length;
				len = Object.keys( $jx2 ).length;
			}
			$( $nwProcessor.selection ).find("#selected-count").text( len + ' records selected' );
			$( $nwProcessor.selection ).find("textarea[name='data']").val( $jt );
		}
	},
	select_record_click_event: function($clicked_element_parent, $clicked_element, $clicked_element_group_selector, shiftctrlKey ){
		
		//Store ID of Selected Row
		var selected_record = $clicked_element.find('.datatables-record-id').attr('id');
		var selected_record_data = {};
		if( $clicked_element.find('textarea.datatables-record-data').val() ){
			selected_record_data = JSON.parse( $clicked_element.find('textarea.datatables-record-data').val() );
		}
		
		if( $nwProcessor.selection ){
			shiftctrlKey = 1;
			var $jx2 = $nwProcessor.get_selection();
		}
		
		if( $clicked_element.hasClass('row_selected') ){
			
			$nwProcessor.single_selected_record = selected_record;
			
			if( $nwProcessor.selection && $nwProcessor.single_selected_record && $jx2 && $jx2[ $nwProcessor.single_selected_record ]  ){
				delete $jx2[ $nwProcessor.single_selected_record ];
			}
			
			if( ! $clicked_element.hasClass('inline-edit-in-progress') ){
				//Mark DataTable Row as Selected
				$clicked_element.removeClass('row_selected');
				
				$nwProcessor.multiple_selected_record_id = $nwProcessor.multiple_selected_record_id.replace( $nwProcessor.single_selected_record + ':::' , '' );
				$nwProcessor.multiple_selected_record_id = $nwProcessor.multiple_selected_record_id.replace( ':::' + $nwProcessor.single_selected_record , '' );
				
				$nwProcessor.single_selected_record = '';
				var sp = $nwProcessor.multiple_selected_record_id.split(":::");
				if( sp[0] ){
					$nwProcessor.single_selected_record = sp[0];
				}
			}
			
			
		}else{
			
			if( shiftctrlKey ){
				if( $nwProcessor.single_selected_record && $nwProcessor.multiple_selected_record_id == '' ){
					$nwProcessor.multiple_selected_record_id = $nwProcessor.single_selected_record;
				}
				
				$nwProcessor.multiple_selected_record_id += ':::' + selected_record;
				$nwProcessor.single_selected_record = selected_record;
			}else{
				$nwProcessor.single_selected_record = selected_record;
				
				//Clear All Previously Selected Rows in DataTable
				$clicked_element_parent.find($clicked_element_group_selector).removeClass('row_selected');
				
				$nwProcessor.multiple_selected_record_id = '';
			}
			
			if( $nwProcessor.selection && $nwProcessor.single_selected_record ){
				if( selected_record_data ){
					$jx2[ $nwProcessor.single_selected_record ] = selected_record_data;
				}else{
					$jx2[ $nwProcessor.single_selected_record ] = 1;
				}
			}
			//Mark DataTable Row as Selected
			$clicked_element.addClass('row_selected');
			
			if( $nwProcessor.single_selected_record && $nwProcessor.record_click_by_user ){
				if( $("#record-details-home-control-handle").hasClass("v3") ){
					if( ! $("#record-details-home-control-handle").hasClass("active") ){
						var firstTabEl = document.querySelector('#record-details-home-control-handle');
						var firstTab = new bootstrap.Tab(firstTabEl);

						firstTab.show();
					}
				}else{
					if( $("#record-details-home-control-handle").is(":visible") && ! $("#record-details-home-control-handle").parent().hasClass("active") ){
						$("#record-details-home-control-handle").click();
					}
				}
				
				if( $(".datatable-split-screen").is(":visible") && $(".datatable-split-screen").attr("action") ){
					
					$.fn.cProcessForm.ajax_data = {
						ajax_data: {id: $nwProcessor.single_selected_record, sel_d:JSON.stringify( selected_record_data ) },
						form_method: 'post',
						ajax_data_type: 'json',
						ajax_action: 'request_function_output',
						ajax_container: '',
						ajax_get_url: $(".datatable-split-screen").attr("action"),
					};
					$.fn.cProcessForm.ajax_send();
				}
			}
			//multiple_selected_record_id
		}
		
		$nwProcessor.set_selection( $jx2 );
		
		//CHECK WHETHER OR NOT TO DISPLAY DETAILS
		//console.log('single', $nwProcessor.single_selected_record);
		//console.log('multiple', $nwProcessor.multiple_selected_record_id);
		
		if( $('#record-details-home').is(':visible') ){
			if( $nwProcessor.single_selected_record && ( $nwProcessor.multiple_selected_record_id == '' || $nwProcessor.multiple_selected_record_id == $nwProcessor.single_selected_record )  ){
				//Replace Container Content with entire record details
				$('#record-details-home')
				.html( $('#main-details-table-'+ $nwProcessor.single_selected_record ).html() );
			}
			
			if( $nwProcessor.multiple_selected_record_id ){
				var array_of_selected_records = $nwProcessor.multiple_selected_record_id.split(':::');
				
				var count = array_of_selected_records.length;
				
				$nwProcessor.details_of_multiple_selected_records = '';
				
				for( var i = 0; i < count; i++ ){
					//Push All Details to display container
					$nwProcessor.details_of_multiple_selected_records += $( '#main-details-table-' + array_of_selected_records[i] ).html();
				}
				
				if( $('#record-details-home').is(':visible') && $nwProcessor.details_of_multiple_selected_records ){
					$('#record-details-home')
					.html( $nwProcessor.details_of_multiple_selected_records );
				}
			}
		}
		
		if( $nwProcessor.single_selected_record ){
			try {
				$.fn.nwRecordDetailsSidePane.populateData( $nwProcessor.single_selected_record );
			}
			catch(err) {
				// Handle error(s) here
			}
			
			if( ! ( $nwProcessor.multiple_selected_record_id == '' || $nwProcessor.multiple_selected_record_id == $nwProcessor.single_selected_record ) ){
				$('#'+ $nwProcessor.single_selected_record)
				.parents('table')
				.attr('tabIndex', 1 )
				.focus();
				
				//$(document).scrollTop(0);
			}
		}
		//return false;
	},
	
	ajax_generate_modules: function(data){
		if(data.fullname.length > 1){
			var icons = [ 'icon-folder-open', 'icon-wrench', 'icon-list', 'icon-calendar' ];
			var icon_count = 0;
			
            
			//PREPARE DATA FOR DISPLAY TO BROWSER
            //<ul id="module-menu" class="nav nav-list bs-docs-sidenav">
			var html = '<li><!-- BEGIN SIDEBAR TOGGLER BUTTON --><div class="sidebar-toggler hidden-phone"></div><!-- BEGIN SIDEBAR TOGGLER BUTTON --></li><li class="start active "><a href="index.html"><i class="icon-home"></i> <span class="title">Dashboard</span><span class="selected"></span></a></li>';
			$.each(data.modules, function(key, value){
				//Exclude user details
				if(key!='user_details'){
                    //html += '<div class="accordion-group">';
                    
					$.each(value, function(ki, vi){
						
						html = html + '<li class=""><a href="javascript:;" data-href="#'+key+'"><i class="'+icons[icon_count]+'"></i> <span class="title">'+ki+'</span><span class="arrow "></span></a><ul class="sub-menu module-menu">';
						$.each(vi, function(k, v){
							switch( v.id ){
							default:
								html = html + '<li><a href="#" id="'+v.id+'" function-id="'+v.id+'" function-class="'+v.phpclass+'" function-name="'+v.todo+'" module-id="'+key+'" module-name="'+ki+'" title="'+v.name+'">'+v.name+' </a></li>';
							break;
							}
						});
						html = html + '</ul></li>';
						
						++icon_count;
						
						if( icon_count > 3 )icon_count = 0;
					});
                    
                    //html += '</div>';
				}
			});
			html = html + '</div>';
			
			//$('#horizontal-nav')
			$('#side-nav')
			.html(html);
			
			//Bind Actions
			$nwProcessor.set_function_click_event();
			
			//Obtain Functiions of Selected Module
			//generate_functions();
			
			//Update Name of Currently Logged In User
			$( '#current-user-name-module' )
			.add( '#user-info-user-name' )
			.text( data.fullname );
			
			var user_info = '';
			
            $('.login-role').text( data.role );
            $('.last-login-time').text( data.login_time );
            
			//user_info += '<div class="row-fluid"><div class="span6">Email</div><div class="span6"><b>'+data.email+'</b></div></div>';
			
			//$('#app-user-information').html( user_info );
			
			$('#appsettings-name').text( data.project_title );
			
			$('title').text( data.project_title );
			
			$('#project-version').text( data.project_version );
			
            $('#toggle-workspace')
            .on('click', function(e){
                e.preventDefault();
                
                $('#side-nav').toggle();
                
                if( $('#side-nav') && $('#side-nav').is(':visible') ){
                    $('#switched-workspace-original-container')
                    .append( $('#switched-workspace') );
                }else{
                    $('#switched-workspace-container')
                    .append( $('#switched-workspace') );
                }
            });
            
         
            //populate dashboard
           $nwProcessor.populate_dashbaord();
            
            App.init(); // initlayout and core plugins
             Index.init();
			 /*
             Index.initJQVMAP(); // init index page's custom scripts
             Index.initCalendar(); // init index page's custom scripts
             Index.initCharts(); // init index page's custom scripts
             Index.initChat();
             Index.initMiniCharts();
             Index.initDashboardDaterange();
             Index.initIntro();
             Tasks.initDashboardWidget();
			 */
		}else{
			//Redirect to login page
			//$.mobile.navigate('#authentication-page');
		}
	},
	
	activateProcessing: function( $button ){
		if( ! $button.hasClass("processing-ajax-request-old") ){
			$button
			.addClass("processing-ajax-request-old");
			
			if( $button.attr("type") && $button.attr("type") == "submit" ){
				$button
				.attr("data-tmp", $button.val() )
				.css( "opacity", 0.3 )
				.val( "Please Wait..." );
			}else{
				$button
				.attr("data-tmp", $button.text() )
				.css( "opacity", 0.3 )
				.text( "Please Wait..." );
			}
		}
	},
		
	deactivateProcessing: function(){
		if( $(".processing-ajax-request-old") ){
			$button = $(".processing-ajax-request-old");
			
			if( $button.attr("type") && $button.attr("type") == "submit" ){
				$button
				.css( "opacity", 1 )
				.val( $button.attr("data-tmp") );
			}else{
				$button
				.css( "opacity", 1 )
				.text( $button.attr("data-tmp") );
			}
			$button.removeClass("processing-ajax-request-old");
		}
	},
		
	set_function_click_event: function(){
		if( ! $nwProcessor.bound_menu_items_to_actions ){
			//Ensure that Menus are bound only once
			$('ul.module-menu')
			.find('a')
			.not('.drop-down')
			.on('click',function( e ){
				e.preventDefault();
				$nwProcessor.set_the_function_click_event( $(this) );
				
			});
			
			$nwProcessor.bound_menu_items_to_actions = 1;
		}
		
		$('a#show-dashboard-page')
		.not(".activated-click-event")
        .on('click', function( e ){
            $nwProcessor.populate_dashbaord();
        })
		.addClass("activated-click-event");
        
		$('a#add-new-record')
		.add('a#generate-report')
		.add('a#import-excel-table')
		.add('a#navigation-pane')
		.add('a#advance-search')
		.add('a#clear-search')
		.add('a#delete-forever')
		.add('.custom-action-button-old')
		.not(".activated-click-event")
		.on('click',function( e ){
			e.preventDefault();
			$nwProcessor.set_the_function_click_event( $(this) );
		})
		.addClass("activated-click-event");
		
		$('#refresh-datatable')
		.not(".activated-click-event")
		.on('click',function( e ){
			e.preventDefault();
			
			var data_table_id = $nwProcessor.getDataTableID( 1 );
			
			if( $('#'+data_table_id) && $('#'+data_table_id).is(":visible") ){
				/* var oTable1 = $('#'+data_table_id).dataTable();
				
				oTable1.fnReloadAjax(); */
				$nwProcessor.reload_datatable();
				$(this).hide();
			}
		})
		.addClass("activated-click-event");
		
		$('a#edit-selected-record')
		.add('#edit-selected-record-password')
		.add('#edit-selected-record-passphrase')
		.add('.custom-single-selected-record-button-old')
		.not(".activated-click-event")
		.on('click',function( e ){
			e.preventDefault();
			
			var record = "";
			
			if( $(this).attr("override-selected-record-only") ){
				record = $(this).attr("override-selected-record");
			}else{
				if( $(this).attr("override-selected-record") ){
					$nwProcessor.single_selected_record = $(this).attr("override-selected-record");
				}
			}
			
			if( ( ! $nwProcessor.single_selected_record  ) && $(this).attr("selected-record") ){
				$nwProcessor.single_selected_record = $(this).attr("selected-record");
			}
			
			if( $nwProcessor.single_selected_record || record ){
				if( $nwProcessor.single_selected_record && ! record )record = $nwProcessor.single_selected_record;
				
				var ok = 1;
				if( $(this).attr('confirm-prompt') ){
					ok = confirm( 'Are you sure that you want to ' + $(this).attr('confirm-prompt') );
				}
				
				if( ok ){
					$nwProcessor.clicked_action_button = $(this);
					
					if( record == "json" ){
						ajax_data = {mod:$(this).attr('mod'), id:record, json:$("body").data("json") };
					}else{
						ajax_data = {mod:$(this).attr('mod'), id:record};
					}
					
					var nt = '';
					if( $(this).attr('new_tab') ){
						nt = $(this).attr('new_tab');
					}
					var nt1 = '';
					if( $(this).attr('new_title') ){
						nt1 = $(this).attr('new_title');
					}else if( $(this).attr('title') ){
						nt1 = $(this).attr('title');
					}
					
					$.fn.cProcessForm.ajax_data = {
						ajax_data: ajax_data,
						form_method: 'post',
						ajax_data_type: 'json',
						ajax_action: 'request_function_output',
						ajax_container: '',
						ajax_get_url: $(this).attr('action'),
						ajax_target: $(this).attr('target'),
						ajax_new_tab: nt,
						ajax_title: nt1,
					};
					$.fn.cProcessForm.ajax_send();
					
					if( $(this).hasClass("one-time-request") ){
						$(this).attr('action', '');
						$(this).removeClass('custom-single-selected-record-button-old');
						$(this).removeClass('custom-single-selected-record-button');
					}
				}
				
			}else{
				$nwProcessor.no_record_selected_prompt();
			}
		})
		.addClass("activated-click-event");	
		
		//Bind Generate Report Buttons Event
		
		$('a.custom-multi-selected-record-button')
		.add('a#generate-report-first-term')
		.not(".activated-click-event")
		.bind('click',function(e){
			e.preventDefault();
			
			if( $nwProcessor.single_selected_record || $nwProcessor.multiple_selected_record_id){
												 
	
				var ok = 1;
					  
	
				if( $(this).attr('confirm-prompt') ){
					ok = confirm( 'Are you sure that you want to ' + $(this).attr('confirm-prompt') );
										 
				}
				
																																																																
	
				if( ok ){
					$nwProcessor.clicked_action_button = $(this);
					
					var budget_id = '';
					var month_id = '';
					
					if( $(this).attr('budget-id') && $(this).attr('month-id') ){
						budget_id = $(this).attr('budget-id');
						month_id = $(this).attr('month-id');
					}
					
					ajax_data = {mod:$(this).attr('mod'), id: $nwProcessor.single_selected_record, ids:$nwProcessor.multiple_selected_record_id, budget:budget_id, month:month_id };
					
					$.fn.cProcessForm.ajax_data = {
						ajax_data: ajax_data,
						form_method: 'post',
						ajax_data_type: 'json',
						ajax_action: 'request_function_output',
						ajax_container: '',
						ajax_get_url: $(this).attr('action'),
					};
					$.fn.cProcessForm.ajax_send();
				}
			}else{
				$nwProcessor.no_record_selected_prompt();
			}
		})
		.addClass("activated-click-event");
		
        //preview-content
		$('a.preview-content')
		.not(".activated-click-event")
		.bind('click',function(e){
			 
			e.preventDefault();
			
			if( $nwProcessor.single_selected_record ){
				var html = $('table#the-main-details-table-'+ $nwProcessor.single_selected_record).find('tr[jid="'+$(this).attr('row-id')+'"]').find('td.details-section-container-value').html();
                
                if( html ){
                    var x=window.open();
                    x.document.open();
                    x.document.write( html );
                    x.document.close();
                }
			}else{
				$nwProcessor.no_record_selected_prompt();
			}
		})
		.addClass("activated-click-event");
		
		//Bind Delete Buttons Event
		$('a#restore-selected-record')
		.add('a#delete-selected-record')
		.not(".activated-click-event")
		.bind('click',function(e){
			e.preventDefault();
			
			if( $nwProcessor.single_selected_record || $nwProcessor.multiple_selected_record_id){
				
				$nwProcessor.clicked_action_button = $(this);
				
				ajax_data = {mod:$(this).attr('mod'), id:$nwProcessor.single_selected_record, ids:$nwProcessor.multiple_selected_record_id};
				
				$.fn.cProcessForm.ajax_data = {
					ajax_data: ajax_data,
					form_method: 'post',
					ajax_data_type: 'json',
					ajax_action: 'request_function_output',
					ajax_container: '',
					ajax_get_url: $(this).attr('action'),
				};
				
				$nwProcessor.confirm_action_prompt = 1;
				
				if( $(this).hasClass('pop-up-button') ){
					$(this).popover('show');
				}
			}else{
				$nwProcessor.no_record_selected_prompt();
			}
		})
		.addClass("activated-click-event");
		
	},
	
	set_the_function_click_event: function($me){
		//Get HTML ID
		$nwProcessor.clicked_menu = $me.attr('id');
		var reason = '';
		
		//Last Clicked function from main menu
		
		switch( $me.attr('id') ){
		case 'add-new-record':
		case 'generate-report':
		case 'import_excel_table':
		case 'restore-all':
		case 'advance-search':
		case 'navigation-pane':
		case 'clear-search':
		case 'add-new-memo-report-letter':
		break;
		default:
			$nwProcessor.clicked_main_menu = $me.attr('id');
		break;
		}
		
		//Get function id
		var function_id = $me.attr('function-id');
		var function_name = $me.attr('function-name');
		var function_class = $me.attr('function-class');
		
		var budget_id = '';
		var month_id = '';
		var operator_id = '';
		var department_id = '';
		var start_date = '';
		var end_date = '';
		var year = '';
		var month = '';
		
		if( $me.attr('budget-id') && $me.attr('month-id') ){
			budget_id = $me.attr('budget-id');
			month_id = $me.attr('month-id');
		}
		
		if( $me.attr('department-id') && $me.attr('operator-id') ){
			operator_id = $me.attr('operator-id');
			department_id = $me.attr('department-id');
		}
		
		if( $me.attr('start-date') && $me.attr('end-date') ){
			start_date = $me.attr('start-date');
			end_date = $me.attr('end-date');
		}
		
		if( $me.attr('year') ){
			year = $me.attr('year');
		}
		
		if( $me.attr('month') ){
			month = $me.attr('month');
		}
		
		column_toggle_table = '';
		column_toggle_num = '';
		column_toggle_name = '';
		
		search_table = '';
		if($me.attr('search-table'))
			search_table = $me.attr('search-table');
		
		if($me.attr('column-toggle-table')){
			column_toggle_table = $me.attr('column-toggle-table');
			column_toggle_name = $me.attr('name');
			column_toggle_num = $me.attr('column-num');
		}	
		
		var module_id = $me.attr('module-id');
		var url = "";
		if( $me.hasClass("custom-action-button-url") ){
			url = $me.attr("href") + '&selected_record=' + $nwProcessor.single_selected_record;
		}
		
		if(function_id && function_id!='do-nothing'){
			//Request Function Output
			$nwProcessor.request_function_output(function_name, function_class, module_id, function_id , budget_id, month_id, operator_id, department_id, url, start_date, end_date, year, month, reason );
			
											
																					 
							  
											
									  
																						

																									   
								 
																																																  

																					   

							 
										 

										
	

		}
	},
	
	request_function_output: function(function_name, function_class, module_id, function_id, budget_id, month_id, operator_id, department_id, url, start_date, end_date, year, month, reason ){
		//IF ADVANCE SEARCH - THEN PASS VALUE OF CURRENT TABLE
		
		switch(function_class){
		case "myexcel":
		case "search":
			ajax_data = {action:function_class, todo:function_name, module:module_id, search_table:search_table};
		break;
		case "column_toggle":
			ajax_data = {action:function_class, todo:function_name, module:module_id, column_toggle_table:column_toggle_table, column_toggle_name:column_toggle_name, column_toggle_num:column_toggle_num};
		break;
		default:
			if(function_id){
				ajax_data = {action:function_class, todo:function_name, module:module_id, id:function_id, budget:budget_id, month:month_id, department:department_id, operator:operator_id, end_date:end_date, start_date:start_date, year:year, month_of_year:month, reason:reason };
			}else{
				ajax_data = {action:function_class, todo:function_name, module:module_id, budget:budget_id, month:month_id, department:department_id, operator:operator_id, end_date:end_date, start_date:start_date, year:year, month_of_year:month, reason:reason };
			}
			
			if( function_name == 'create_new_record' && $nwProcessor.single_selected_record ){
				ajax_data = {action:function_class, todo:function_name, module:module_id, id: $nwProcessor.single_selected_record };
			}
		break;
		}
		
		class_action = function_name;
		class_name = function_class;
		module_id = module_id;
		
		form_method = 'get';
		ajax_data_type = 'json';
		
		//if(function_name=='rename_entity' && before==1)ajax_data_type = 'text'; //NEW RECORD
		//if(function_name=='new')ajax_data_type = 'text'; //NEW RECORD
		//if(function_name=='select_audit_trail')ajax_data_type = 'text'; //NEW RECORD
		//if(function_name=='search')ajax_data_type = 'text'; //SEARCH RECORD
		//if(function_name=='clear_search')ajax_data_type = 'text'; //SEARCH RECORD
		//if(function_name=="column_toggle")ajax_data_type = 'text'; //TOGGLE COLUMN RECORD
		//if(function_name=="delete_forever")ajax_data_type = 'text'; //TOGGLE COLUMN RECORD
		
		ajax_action = 'request_function_output';
		ajax_container = '';
		ajax_get_url = '';
		
																										
												  
											
												  
											  
   
		if( url ){
			ajax_get_url = url;
			ajax_data = {};
						
		
						

																  
									  
				

				  
		}
		if( $nwProcessor.single_selected_record ){
			if( url ){
				ajax_get_url += '&sel_id='+$nwProcessor.single_selected_record;
			}else{
				ajax_data.sel_id = $nwProcessor.single_selected_record;
			}
		}
		
		$.fn.cProcessForm.ajax_data = {
			ajax_data: ajax_data,
			form_method: form_method,
			ajax_data_type: 'json',
			ajax_action: 'request_function_output',
			ajax_container: '',
			ajax_get_url: ajax_get_url,
		};
		$.fn.cProcessForm.ajax_send();
	},
	
	re_process_previous_request: function( data ){
        
		if( data.re_process && ! $nwProcessor.cancel_ajax_recursive_function ){
			if( data.re_process_code )$nwProcessor.trigger_new_ajax_request( data );
			else $nwProcessor.set_the_function_click_event( $( data.re_process ) );
		}else{
			/* //Reload DataTable
			if( data.reload_table && $nwProcessor.oTable ){
				//$nwProcessor.oTable.fnReloadAjax();
				$nwProcessor.reload_datatable();
			} */
		}
	},
	
	trigger_new_ajax_request: function( data ){
		
		ajax_data = {mod:data.mod, id:data.id};
		form_method = 'post';
		
		ajax_data_type = 'json';
		ajax_action = 'request_function_output';
		ajax_container = '';
		ajax_get_url = data.action;
		
		$.fn.cProcessForm.ajax_data = {
			ajax_data: ajax_data,
			form_method: form_method,
			ajax_data_type: 'json',
			ajax_action: 'request_function_output',
			ajax_container: '',
			ajax_get_url: ajax_get_url,
		};
		$.fn.cProcessForm.ajax_send();
	},
	
	tmp_data:'',
	ajax_request_function_output: function(data){
		//alert(data);
		//Close Pop-up Menu
		
		data.reload_table = 1;
		
		if( data.status ){
			tmp_data = data;
			
			switch(data.status){
			case "got-quick-details-view":
				//Update Create New School Button Attributes
				if( $('#custom-details-display-container') && data.html ){
					$('#custom-details-display-container')
					.html( data.html );
                    data.reload_table = 0;
				}
			break;
			case "display-appsettings-setup-page":
				//Update Create New School Button Attributes
				if( $('#create_new_appsettings') && data.create_new_appsettings_data ){
					$('#create_new_appsettings')
					.attr('function-class', data.create_new_appsettings_data.function_class )
					.attr('function-name', data.create_new_appsettings_data.function_name )
					.attr('module-id', data.create_new_appsettings_data.module_id );
					
					//Bind Click Event of Button
					$('#create_new_appsettings')
					.bind('click',function(){
						$nwProcessor.set_the_function_click_event($(this));
					});
				}
				
			break;
			case "display-data-capture-form":
				//Update Create New School Button Attributes				
				if( data.do_not_reload_table )
					data.reload_table = 0;
					
				$nwProcessor.prepare_new_record_form(data);
				
				if( data.country )
					$nwProcessor.activate_country_select_field();
				
				//Display Form Tab
				if( $("#form-home-control-handle").hasClass("v3") ){
					if( ! $("#form-home-control-handle").hasClass("active") ){
						var firstTabEl = document.querySelector('#form-home-control-handle');
						var firstTab = new bootstrap.Tab(firstTabEl);

						firstTab.show();
					}
				}else{
					$('#form-home-control-handle')
					.click();
				}
			break;
			case "display-advance-search-form":
				//Update Create New School Button Attributes
				$nwProcessor.prepare_new_record_form(data);
				
                if( $('#side-nav') && $('#side-nav').is(':visible') ){
                    //Display Top Accordion
                    if( ! $('#collapseTop').hasClass('in') ){
                        $('#collapseTop')
                        .collapse('show');
                    }
                    
                    $('#collapseBottom')
                    .find('.portlet-body')
                    .hide();
				}
				//Display Form Tab
				if( $("#form-home-control-handle").hasClass("v3") ){
					if( ! $("#form-home-control-handle").hasClass("active") ){
						var firstTabEl = document.querySelector('#form-home-control-handle');
						var firstTab = new bootstrap.Tab(firstTabEl);

						firstTab.show();
					}
				}else{
					$('#form-home-control-handle')
					.click();
				}
				
				//bind advance search controls
				$nwProcessor.bind_search_field_select_control();
			break;
			case "modify-appsettings-settings":
				//Update Create New School Button Attributes
				$nwProcessor.prepare_new_record_form(data);
				
				//Update Application with School Properties
				$nwProcessor.update_application_with_school_properties( data );
			break;
			case "redirect-to-dashboard":
				if( data.reload ){
					document.location = document.location + "?activity=update";
				}
				
				//Redirect to dashboard page
				$('#page-body-wrapper')
				.html( data.html );
				
				//Update Application with School Properties
				$nwProcessor.update_application_with_school_properties( data );
				
				//Get Menus
				$nwProcessor.generate_modules();
				
				//Activate Tabs
				$('#myTab')
				.find('a')
				.bind('click', function (e) {
				  e.preventDefault();
				  $(this).tab('show');
				  
				   //Link Tab Clicks to Accordion
				   if( ! $('#collapseTop').hasClass('in') ){
						$('#collapseTop')
						.collapse('show');
					}
				});
				
				//bind clear tab contents button
				$('#clear-tab-contents')
				.bind('click', function (e) {
					e.preventDefault();
					
					$('.tab-content')
					.find('div.active.tab-content-to-clear')
					.empty();
						
					$('.tab-content')
					.find('div.active')
					.find('.tab-content-to-clear')
					.empty();
						
				});
			break;
			case "reload-page":
				document.location = document.location;
			break;
			case "redirect-to-login":
				//Redirect to login page
				$nwProcessor.prepare_new_record_form(data);
				
				//Update Application with School Properties
				$nwProcessor.update_application_with_school_properties( data );
			break;
			case "authenticate-user":
				//Refresh Form Token
				//refresh_form_token( data );
			break;
			case "displayed-dashboard":
				//Refresh Form Token
				$('#data-table-container')
				.html( data.html );
                
                $nwProcessor.bind_details_view_control();
			break;
			case "deleted-records":
				var data_table_id = $nwProcessor.getDataTableID( 1 );
				
				var oTable1 = $('#'+data_table_id).dataTable();
				if( oTable1 ){
					//oTable1.fnReloadAjax();
					$nwProcessor.reload_datatable();
					
					if( data.reload_other_tables ){
						$.each( data.reload_other_tables, function( k, v ){
							var oTable2 = $('#'+v+'-datatable').dataTable();
							oTable2.fnReloadAjax();
						});
					}
				}
			break;
			case "column-toggle":
				$nwProcessor.ajax_hide_show_column_checkbox( data );
                $nwProcessor.bind_details_view_control();
			break;
			case "reload-datatable":
				//Activate DataTables Plugin
				if( data.searched_table ){
					class_name = data.searched_table;
				
					$('#search-query-display-container')
					.html( data.search_query )
					.attr( 'title', $('#search-query-display-container').text() );
				}
				
				if( $('#collapseTop').hasClass('in') ){
					$('#collapseTop')
					.collapse('hide');
				}
				if( ! $('#collapseBottom').hasClass('in') ){
					$('#collapseBottom')
					.collapse('show');
				}
				$nwProcessor.reload_datatable();
			break;
			case "display-datatable":
				
				if( $('#collapseTop').hasClass('in') ){
					$('#collapseTop')
                    .find('.portlet-body')
                    .hide();
                    
                    $('#collapseTop')
                    .find('.collapse')
                    .removeClass('collapse')
                    .addClass('expand');
				}
				if( ! $('#collapseBottom').hasClass('in') ){
					$('#collapseBottom')
					.collapse('show');
				}
				
				//Display HTML
				var html = data.html;
				
				if( data.inline_edit_form ){
					html += data.inline_edit_form;
				}
				
				$('#data-table-container')
				.html( html );
				
				if( data.search_query )
					$('#search-query-display-container')
					.html( data.search_query )
					.attr( 'title', $('#search-query-display-container').text() );
				
				
				//Activate DataTables Plugin
				$nwProcessor.recreateDataTables();
				
				$nwProcessor.set_function_click_event();
				
				//UPDATE HIDDEN / SHOWN COLUMNS
				$nwProcessor.update_column_view_state();
				
			break;
			case "saved-form-data":
				if( data.typ == 'serror' || data.typ == 'uerror' ){
					break;
				}
				
				if( $('#collapseTop').hasClass('in') ){
					$('#collapseTop')
					.collapse('hide');
				}
				if( ! $('#collapseBottom').hasClass('in') ){
					$('#collapseBottom')
					.collapse('show');
				}
				
				//Check for saved record id
				if( data.saved_record_id ){
					$nwProcessor.single_selected_record = data.saved_record_id;
				}
				
				if( $('form.quick-edit-form') && data.clear_stepmaxstep ){
					$('form.quick-edit-form')
					.find('input[name="stepmaxstep"]')
					.val( data.clear_stepmaxstep );
				}
				
				//Refresh Token
				if( data.go_to_next_record ){
					$('#'+ $nwProcessor.single_selected_record ).parents('tr').next().click();
				}
				
				var data_table_id = $nwProcessor.getDataTableID( 1 );
				
				if( data.do_not_reload_table ){
					data.reload_table = 0;
					
					$('.dynamic')
					.find('#refresh-datatable')
					.show();
					
				}else{
					//Reload DataTable
					if( $('#'+data_table_id) && $('#'+data_table_id).is(":visible") ){
						/* var oTable1 = $('#'+data_table_id).dataTable();
						
						oTable1.fnReloadAjax(); */
						$nwProcessor.reload_datatable();
						//recreateDataTables();
						
						//Bind form submission event
						$nwProcessor.select_record_click_function( $('#'+data_table_id) );
					}
					if( data.reload_other_tables ){
						$.each( data.reload_other_tables, function( k, v ){
							var oTable2 = $('#'+v+'-datatable').dataTable();
							oTable2.fnReloadAjax();
						});
					}
					
					//Bind details open and close event to table
					$nwProcessor.bind_details_button_click();
				}
				
				if( data.javascript_functions ){
					
					$.each( data.javascript_functions , function( key, value ){
						eval( value + "()" );
					} );
				}
			break;
			case "download-report":	
				if( $('#monthly-report-link-con') && $('#monthly-report-link-con').is(':visible') && data.html ){
					$('#monthly-report-link-con').html( data.html );
				}
			break;
			}
		
		}else{
			data.reload_table = 0;
		}
		
		
		//Handle / Display Error Messages / Notifications
		$.fn.cProcessForm.display_notification( data );
		
		//Check for re-process command
		$nwProcessor.re_process_previous_request( data );
		
		tmp_data = {};
	},
	
	reload_datatable: function(){
		var data_table_id = $nwProcessor.getDataTableID( 1 );
						   
		if( data_table_id && data_table_id != 'undefined-datatable' ){
			var oTable1 = $('#'+data_table_id).dataTable();
			data_table_id = data_table_id.replace( "-datatable", "" );
			//console.log(data_table_id);
			/* 
			if( $("#datatable-split-screen-" + data_table_id) && $("#datatable-split-screen-" + data_table_id).is(":visible") ){
				$("#datatable-split-screen-" + data_table_id).html('');
			}
			 */
			if( oTable1 ){
				oTable1.fnReloadAjax();
				//oTable1.fnAdjustColumnSizing();
				
				if( $nwProcessor.selection ){
					$nwProcessor.rehighlight_selected_record_function( 1 );
				}
			}else{
				$nwProcessor.recreateDataTables();
			}
		}
	},
	
	//Function to Update Application with School Properties
	update_application_with_school_properties: function( data ){
		//Update school properties if set
		if( data.appsettings_properties ){
			$('#appsettings-name')
			.text( data.appsettings_properties.appsettings_name );
		}
	},
	
	//Function to Refresh Form Token After Processing
	refresh_form_token: function( data ){
		//Update school properties if set
		if( data.tok && $('form') ){
			$('form')
			.find('input[name="processing"]')
			.val( data.tok );
		}
	},
	
	/******************************************************/
	/****************DISPLAY DETAILS OF ROW****************/
	/******************************************************/
	fnFormatDetails: function( oTable, nTr, details, img, duration ){
		var aData = oTable.fnGetData( nTr );
		sOut = '<div class="grid-inner-content">'+details+'</div>';
		
		return sOut;
	},
	/******************************************************/
	
	bind_details_button_click: function(){
		/******************************************************/
		/********LISTENER FOR OPENING & CLOSING DETAILS********/
		/******************************************************/
		
		$('.nwp-datatables-direct-click').off('click');
		
		$('.datatables-details').on('click', function () {
			
			if($(this).data('details')!='true'){
				$(this)
				.data('details','true');
				
				var nTr = $(this).parents('tr')[0];
				$nwProcessor.oTable.fnOpen( nTr, $nwProcessor.fnFormatDetails( $nwProcessor.oTable, nTr, $(this).next('div').html(), $(this).next('div').next('div').html(),$(this).next('div').next('div').next('div').html()), 'details' );
				
			}else{
				$(this)
				.data('details','false');
				
				var nTr = $(this).parents('tr')[0];
				$nwProcessor.oTable.fnClose( nTr );
			}
		} );
	},
	
	//Display Notification Message
	notificationTimerID:'',
	
	prepare_new_record_form: function( data ){
		
		//Prepare and Display New Record Form
		$('#form-content-area')
		//.html('<div id="form-panel-wrapper1">'+data.html+'</div>')
		.html(data.html);
		
		//Bind Html text-editor
		$.fn.cProcessForm.activateAjaxForm();
		
		$(".tab-content.right-side").scrollTop(0);
	},
		
	prepare_new_record_form_new: function(){
		$.fn.cProcessForm.activateAjaxForm();
		$(".tab-content.right-side").scrollTop(0);
	},
	
	reload_table_after_form_submit: 1,
	
	//Bind Show/Hide Column Checkboxes to Show/Hide Column Menu Button
	
	bind_show_hide_column_checkbox: function(){
		
		$("body")
		.on( "click", 'ul.show-hide-column-con input[type="checkbox"]', function(e){	
			//get current column
			var col = $(this).parents('li').index();
			
			$(this).attr('column-num' , col);
			$(this).attr('id' , 'col-' + col );
			
			$nwProcessor.set_the_function_click_event( $(this) );
			
			$nwProcessor.oTable.fnAdjustColumnSizing();
		});
		
		//bind delete popover buttons
		$("body")
		.on( "click", 'input#delete-button-yes', function(e){	
			if( $('a#delete-selected-record').hasClass('pop-up-button') ){
				$('a#delete-selected-record').popover('hide');
			}
			
			if( $nwProcessor.confirm_action_prompt ){
				$.fn.cProcessForm.ajax_send();
				
				//Reset confirmation value once pop-up closes
				confirm_action_prompt = 0;
			}
		});
		
		$("body")
		.on( "click", 'input#restore-button-yes', function(e){
			if( $('a#restore-selected-record').hasClass('pop-up-button') ){
				$('a#restore-selected-record').popover('hide');
			}
			
			if( $nwProcessor.confirm_action_prompt ){
				$.fn.cProcessForm.ajax_send();
				
				//Reset confirmation value once pop-up closes
				confirm_action_prompt = 0;
			}
		});
		
		$("body")
		.on( "click", 'input#delete-button-no', function(e){
			if( $('a#delete-selected-record').hasClass('pop-up-button') ){
				$('a#delete-selected-record').popover('hide');
			}
		});
		
		$("body")
		.on( "click", 'input#restore-button-no', function(e){
			if( $('a#restore-selected-record').hasClass('pop-up-button') ){
				$('a#restore-selected-record').popover('hide');
			}
		});
		
		//Bind Cancel Operation for recursive ajax requests
		$("body")
		.on( "click", 'button.stop-current-operation', function(e){
			$nwProcessor.cancel_ajax_recursive_function = true;
		});
	},
	
	//Bind Search Field Select Control
	bind_search_field_select_control: function(){
		$('#search-field-select-combo')
		.on('change',function(){
			$('form')
			.find('.default-hidden-row')
			.hide();
			
			$('form')
			.find('.'+$(this).val())
			.show();
		});
	},
	
	//Hide / Show Column after serverside processing
	ajax_hide_show_column_checkbox: function( data ){
		var data_table_id = $nwProcessor.getDataTableID( 1 );
		
		//get current column
		var col = data.column_num;
		++col;
		++col;
		$nwProcessor.fnShowHide( col , data_table_id );
		
		//Toggle Check Box State
		$('#show-hide-column-con')
		.find('input[name="'+data.column_name+'"]')
		.attr('checked', data.column_state);
		
		var parent = $('#show-hide-column-con').find('input[name="'+data.column_name+'"]').parents('.ui-checkbox');
		
	},
		
	//Update Hidden / Show Columns
	update_column_view_state: function(){
		
					
														  
			   
   

													 
			   
   

		$('ul.show-hide-column-con')
		.find('input[type="checkbox"]')
		.not('.nw-skip')
		.each(function(){
			

			if( $(this).attr('name') ){
				if(!$(this).is(':checked')){
					//get current column
					var col = $(this).parents('li').index();
					col += 2;
				
									 
																									  
					$nwProcessor.fnHide(col , $(this).parents('ul').attr("data-table") );
				}
			}
		});
		
		/* 
		var data_table_id = $nwProcessor.getDataTableID( 1 );
		var oTable1 = $('#'+data_table_id).dataTable();
		oTable1.fnAdjustColumnSizing();
		 */
	},
	
	//Column Selection
	fnShowHide: function( iCol, dataTableName )
	{
		/* Get the DataTables object again - this is not a recreation, just a get of the object */
		var oTable1 = $('#'+dataTableName).dataTable();
		 
		var bVis = oTable1.fnSettings().aoColumns[iCol].bVisible;
		oTable1.fnSetColumnVis( iCol, bVis ? false : true );
		
	},
	
	//Column Selection
	fnHide: function( iCol, dataTableName )
	{
		/* Get the DataTables object again - this is not a recreation, just a get of the object */
		var oTable1 = $('#'+dataTableName).dataTable();
		oTable1.fnSetColumnVis( iCol, false, false);
	},
	
	initiate_tiny_mce_for_popup_textarea: function( selector ){
		
		$( selector ).tinymce({
			// Location of TinyMCE script
			script_url : 'js/tiny_mce/tinymce.min.js',
			
			// General options
			theme: "modern",
			height : 280,
			width : 520,
			plugins: [
					"advlist autolink autosave link image lists charmap print preview hr anchor pagebreak spellchecker",
					"searchreplace wordcount visualblocks visualchars code fullscreen insertdatetime media nonbreaking",
					"table contextmenu directionality emoticons template textcolor paste fullpage textcolor"
			],

			toolbar1: "newdocument | bold italic underline strikethrough | alignleft aligncenter alignright alignjustify | styleselect formatselect fontselect fontsizeselect",
			toolbar2: "cut copy paste | searchreplace | bullist numlist | outdent indent blockquote | undo redo | link unlink image | inserttime preview fullpage | forecolor backcolor",
			toolbar3: "table | hr removeformat | subscript superscript | charmap emoticons | spellchecker | pagebreak restoredraft",

			menubar: false,
			toolbar_items_size: 'small',

			style_formats: [
					{title: 'Bold text', inline: 'b'},
					{title: 'Red text', inline: 'span', styles: {color: '#ff0000'}},
					{title: 'Red header', block: 'h1', styles: {color: '#ff0000'}},
					{title: 'Example 1', inline: 'span', classes: 'example1'},
					{title: 'Example 2', inline: 'span', classes: 'example2'},
					{title: 'Table styles'},
					{title: 'Table row 1', selector: 'tr', classes: 'tablerow1'}
			],

			templates: [
					{title: 'Test template 1', content: 'Test 1'},
					{title: 'Test template 2', content: 'Test 2'}
			]

		});
	},
	
	no_record_selected_prompt: function(){
		//alert('display prompt that no record was selected');
		var data = {theme:'alert-info note note-info', err:'No Selected Record', msg:'Please select a record by clicking on it', typ:'jsuerror' };
		$.fn.cProcessForm.display_notification( data );
	},
	
	no_function_selected_prompt: function(theme, message_title, message_message, auto_close){
		//alert('display prompt that no record was selected');
		
	},
	
	getDataTableID: function( type ){
		switch( type ){
		case 1:
			if( $nwProcessor.tmp_data && $nwProcessor.tmp_data.data_table_name ){
				return $nwProcessor.tmp_data.data_table_name + '-datatable';
			}
		break;
		}
		
		return $('#dynamic').attr("data-table") + "-datatable";
	},
	
	selection: '',
	recreateDataTables: function(){
		//INITIALIZE DATA TABLES
		var data_table_id = $nwProcessor.getDataTableID( 1 );
		//console.log( data_table_id,  $('#'+data_table_id) );
		var tb = $('#'+data_table_id).attr("class-name");
		var h = parseFloat( $( '#' + $('#'+data_table_id).attr('container') ).css('height').replace('px', '') );
		var h1 = "350";
		
		if( ! isNaN( h ) ){
			h1 = h - 150; //94 //39,25 150 //NB:account for table header height
		}
		
		var sel = 0;
		var md = {};
		
		if( $('textarea#' + data_table_id + '-attributes' ).val() ){
			md = JSON.parse( $('textarea#' + data_table_id + '-attributes' ).val() );
			if( typeof( md ) !== 'object' ){
				var md = {};
			}
		}
		
		md["table"] = tb;
		
		$nwProcessor.selection = '';
		if( $("form#datatable-select-all").is(":visible") ){
			md[ "selection" ] = 1;
			$nwProcessor.selection = "form#datatable-select-all";
			
			$('input#datatable-select-all-checkbox')
			.off('change')
			.on('change', function(){
				if( $(this).is(":checked") ){
					$nwProcessor.select_all_records();
				}else{
					$nwProcessor.deselect_all_records();
				}
			});
		}

		var surl = $.fn.cProcessForm.requestURL + "Endpoint?action=display_table&current_tab=datatable";
		if( $.fn.cProcessForm.customURL ){
			surl = $.fn.cProcessForm.requestURL + "Endpoint?action=display_table&current_tab=datatable";
		}
		
		//
		$nwProcessor.oTable = $('#'+data_table_id).dataTable({
			"sServerMethod": "POST",
			"more_data": md,
			"bProcessing": true,
			"bServerSide": true,
			"sAjaxSource": surl,
			"sScrollY": h1,
			"sPaginationType": "full_numbers",
			"sScrollX": "100%",
			"bJQueryUI": true,
			"bDestroy": true,
			//"sDom": "Rlfrtip",
			"sDom": 'R<"H"lfr>t<"F"ip>',
			"bStateSave": false,
			"iDisplayLength": 25,
			 "aoColumnDefs": [ 
				 { "bSortable": false, "aTargets": [ 0 ] }
			   ],
			/*
			THIS CODE HAS TO BE INSERTED BEFORE THE TABLE REFRESHES
				//Return inline-edit fields to their container
				if( $('#inline-edit-form-wrapper') && $('.inline-form-element') ){
					$('.inline-form-element')
					.removeClass('inline-form-element')
					.addClass('form-gen-element')
					.appendTo(	$('#inline-edit-form-wrapper').find('form') );
				}
			*/
			"fnInitComplete": function () {
				//Store Normal Table
				//$('#cloned-content-area').html($('#content-area').html());
				/*
				new FixedColumns( oTable, {
					"iLeftColumns": 4,
					"iLeftWidth": 450,
				} );
				*/
				$('#'+ $(this).attr("id") +'_filter')
				.find('input')
				.attr({
					placeholder:"Quick Search",
					title:"Perform quick search",
				})
				.addClass("form-control input-sm");
				
				$('#'+$(this).attr("id")+'_length')
				.find('select')
				.css({
					'padding': '2px',
					'margin': '2px',
					'width': 'auto',
				});
				
				//UPDATE MORE DETAILS COLUMN SELECTOR
				$nwProcessor.create_field_selector_control( $(this) );
				
				$('a.pop-up-button').on('click', function(e){
					if( $('#refresh-datatable').is(":visible") ){					
						$('#refresh-datatable')
						.click();	
					}
					
					e.preventDefault();
				});
				
				$('.pop-up-button').popover({
					html:true,
					container:'#data-table-section',
					content:function(){
						if( ! $(this).data("insert") ){
							var html = $(this).next('div.pop-up-content').html();
							$(this).data("insert", 1);
							return html;
						}
					},
				});
				
                if( $('.dropdown-toggle') ){
                    $('.dropdown-toggle').dropdown();
                }
				/*
				setTimeout( function(){ 
					$(".dataTables_scrollBody").find(".dataTable.display").dataTable().fnAdjustColumnSizing(); 
					setTimeout( function(){ 
						$(".dataTables_scrollBody").find(".dataTable.display").dataTable().fnAdjustColumnSizing(); 
					}, 800 );
				}, 2000 );
                */
				//setTimeout( function(){ nwResizeWindow.resizeWindow(); $nwProcessor.oTable.fnAdjustColumnSizing(); }, 300 );
				
			},
			"fnDrawCallback": function() {
			   //Optimize Search Field
				$nwProcessor.select_record_click_function( $(this) );
		
				//Bind details open and close event to table
				$nwProcessor.bind_details_button_click();
				
				$nwProcessor.bind_details_view_control();
                
				//Rehighlight Selected Record
				$nwProcessor.rehighlight_selected_record_function( $(this) );
				
				$nwProcessor.bind_page_up_down_scrolling( $(this) );
			}
		
		})
		.addClass("activated-table");
		
	},
	
	bind_popover_controls: function(){
		var $parent = $(this).parent();
		
		$('a.pop-up-button-control')
		.not("bound")
		.on("click", function(){
			//if( $(this).data("remove") )
				$(this).attr("function-id","");
			//else
				$(this).data("remove",1);
		});
		
		$('a.pop-up-button-control')
		.not("bound")
		.popover({
			html:true,
			container:$("#form-home-control-handle"),
			content:function(){
				if( ! $(this).data("insert") ){
					var html = $(this).next('div.pop-up-content').html();
					$(this).data("insert", 1);
					$(this).next('div.pop-up-content').remove();
					return html;
				}
			},
		})
		.addClass("bound");;
		
	},
	
	bind_details_view_control: function(){
        //Bind Delete Buttons Event
        if( $('a.quick-details-field') ){
            $('a.quick-details-field')
            .bind('click',function(e){
                e.preventDefault();
                
                ajax_data = {};
                ajax_get_url = $(this).attr('action');
				
				$.fn.cProcessForm.ajax_data = {
					ajax_data: ajax_data,
					form_method: 'post',
					ajax_data_type: 'json',
					ajax_action: 'request_function_output',
					ajax_container: '',
					ajax_get_url: ajax_get_url,
				};
				$.fn.cProcessForm.ajax_send();
            });
        }
    },
	
	activate_custom_view_select_button: function(){
		$('#custom-view-select')
		.find('a.custom-view-select-button')
		.bind('click',function(e){
			e.preventDefault();
			
			$('#custom-view-select-text')
			.text( $(this).text() );
			
			if ($(this).hasClass('hide-selected') && $(this).attr('data-class')) {
				var targetClass = $(this).attr('data-class');
				$(document).find(targetClass)
				.addClass('hide-custom-view-select-classes');
			}

			
			if( $(this).hasClass('show-selected') && $(this).attr('data-class') ){
				$('.hide-custom-view-select-classes')
				.removeClass('hide-custom-view-select-classes');
				
				$( "tr.even, tr.odd" )
				.not( $(this).attr('data-class') )
				.addClass( 'hide-custom-view-select-classes' );
			}
			
			if( $(this).hasClass('show-all') ){
				$('.hide-custom-view-select-classes')
				.removeClass('hide-custom-view-select-classes');
			}
			
		});
	},
    
	//$nwProcessor.bind_page_up_down_scrolling();
	bind_page_up_down_scrolling: function( $context ){
		
		//$context
		//.not('.keyed-down')
		$(document)
		.off('keydown')
		.on('keydown', function(e){
			//get selected record
			if( ! $nwProcessor.single_selected_record )return;
			var allow_scroll = 0;
			/*
			var allow_scroll = 0;
			if( ( e.keyCode == 40 || e.keyCode == 38 ) && $('form.quick-edit-form').find('input, select, textarea').is(':focus') ){
				allow_scroll = 1;
				
				$('form.quick-edit-form')
				.find('input[name="stepmaxstep"]')
				.val( '10' );
			}
			*/
			if( ! $('input, select, textarea').is(':focus') || allow_scroll ){
				
				var scrollTopAmountLarge = 300;
			
				var scrollTopAmountSmall = 50;
				
				var scrollTopAmount = 0;
				var scrollAlongAmount = 0;
				
				var scrollTopToSelectedRow = 0;
				
				var current_view;
				//var $element = $(this).parents(".dataTables_scrollBody");
				//var $element_to_scroll = $(this);
				
				var $element = $("#dynamic");
				var $element_to_scroll = $("#dynamic").find(".dataTables_scrollBody");
				
				current_view = 'table';
				//console.log(22);
				
				switch(e.keyCode){
				case 35:	//End key
					scrollAlongAmount = $element.width();
				break;
				case 36:	//Home key
					scrollAlongAmount = $element.width() * -1;
				break;
				case 37:	//Left arrow
					scrollAlongAmount = (scrollTopAmountSmall * -1);
				break;
				case 38:	//Up arrow
					scrollTopAmount = (scrollTopAmountSmall * -1);
					scrollTopToSelectedRow = 2;
				break;
				case 39:	//Right arrow
					scrollAlongAmount = (scrollTopAmountSmall);
				break;
				case 40:	//Down arrow
					scrollTopAmount = scrollTopAmountSmall;
					scrollTopToSelectedRow = 1;
				break;
				case 34:	//Page down button
					//scrollTopAmount = scrollTopAmountLarge;
				break;
				case 33:	//Page up button
					//scrollTopAmount = (scrollTopAmountLarge * -1);
				break;
				case 65:	//A Ctrl [17]
					if(e.ctrlKey){
						e.preventDefault();
						$nwProcessor.select_all_records();
						return false;
					}
				break;
				}
				
				if(scrollTopAmount){
					var scrollPosition = $element_to_scroll.scrollTop();
					
					if( scrollTopToSelectedRow && $nwProcessor.single_selected_record ){
						e.preventDefault();
						
						if( $('#'+ $nwProcessor.single_selected_record) ){
							
							switch( scrollTopToSelectedRow ){
							case 1:
								$('#'+ $nwProcessor.single_selected_record).parents('tr').next().click();
								scrollTopToSelectedRow = $('#'+ $nwProcessor.single_selected_record).parents('tr').height();
							break;
							case 2:
								$('#'+ $nwProcessor.single_selected_record).parents('tr').prev().click();
								scrollTopToSelectedRow = $('#'+ $nwProcessor.single_selected_record).parents('tr').height() * -1;
							break;
							}
						}else{
							scrollTopToSelectedRow = 0;
						}
					}else{
						scrollTopToSelectedRow = 0;
					}
					
					scrollTopToSelectedRow  = 0;
					scrollPosition = 0;
					var ref = $element.offset().top;
					scrollTopAmount = $element.scrollTop() + $('#'+ $nwProcessor.single_selected_record).parents('tr').offset().top - ref;
				
					if( scrollTopToSelectedRow ){
						$element_to_scroll
						.scrollTop( scrollPosition + scrollTopToSelectedRow );
					}else{
						$element
						.scrollTop(scrollPosition + scrollTopAmount);
					}
				}
				
				if(scrollAlongAmount){
					var scrollPosition = $element_to_scroll.scrollLeft();
					
					$element_to_scroll
					.scrollLeft(scrollPosition + scrollAlongAmount);
				}
				
				scrollTopAmount = 0;
				scrollAlongAmount = 0;
			}
		})
		.addClass('keyed-down');
		/*
		$('#example_wrapper')
		.find('.dataTables_scrollBody')
		.bind('keydown',function(e){
			alert(e.code);
			$(this).scrollTop($(this).scrollTop()+scrollTopAmountLarge);
		});
		*/
	},
	
	deselect_all_records: function(){
		var $element = $( "#" + $nwProcessor.getDataTableID( 1 ) );
		
		$nwProcessor.single_selected_record = '';
		$nwProcessor.multiple_selected_record_id = '';
		
		if( $nwProcessor.selection ){
			$nwProcessor.set_selection( {} );
		}
		
		$element
		.find('tr')
		.removeClass('row_selected');
	},	
	deselect_all_records_and_clear_form: function(){
		$nwProcessor.deselect_all_records();
		var $element = $( "#" + $nwProcessor.getDataTableID( 1 ) );
		//console.log( "#" + $nwProcessor.getDataTableID( 1 ) );
		
		$element
		.parents(".dynamic")
		.find('form#datatable-select-all')
		.trigger("reset").find(".file-content").html('');
		
		$element
		.parents(".dynamic")
		.find('form#datatable-select-all')
		.find(".form-gen-element-image-upload-preview")
		.attr('src','');
	},	
	select_all_records: function(){
		var $element = $( "#" + $nwProcessor.getDataTableID( 1 ) );
		$nwProcessor.multiple_selected_record_id = '';
		
		$nwProcessor.details_of_multiple_selected_records = '';
		var $jx2 = $nwProcessor.get_selection();
		
		$element
		.find('tr:visible')
		.each(function(){
			var id_of_record = $(this).find('.datatables-record-id').attr('id');
			if( id_of_record ){
				var selected_record_data = {};
				if( $(this).find('textarea.datatables-record-data').val() ){
					selected_record_data = JSON.parse( $(this).find('textarea.datatables-record-data').val() );
				}
				
				if( selected_record_data ){
					$jx2[ id_of_record ] = selected_record_data;
				}else{
					$jx2[ id_of_record ] = 1;
				}
				$nwProcessor.multiple_selected_record_id = $nwProcessor.multiple_selected_record_id +':::'+id_of_record;
				
				//Push All Details to display container
				var passed_value = $('#main-details-table-'+id_of_record).html();
				if( passed_value )
					$nwProcessor.details_of_multiple_selected_records += passed_value;
			}
		});
		
		$nwProcessor.set_selection( $jx2 );
		
		if( $('#record-details-home').is(':visible') && $nwProcessor.details_of_multiple_selected_records ){
			$('#record-details-home')
			.html( $nwProcessor.details_of_multiple_selected_records );
		}
		
		$element
		.find('tr')
		.addClass('row_selected');
	},
	scroll_to_top_of_selected_record: function(){
		var scrollTopAmount = 0;
		var ref = 0;
		
		var scrollTopToSelectedRow = 0;
		
		if( $nwProcessor.single_selected_record && $('#'+ $nwProcessor.single_selected_record) ){
			var $element_to_scroll = $( "table#" + $('#'+ $nwProcessor.single_selected_record).parents(".dynamic").attr("data-table") );
			var $element = $('#'+ $nwProcessor.single_selected_record).parents(".dataTables_scrollBody");
			scrollTopToSelectedRow = $('#'+ $nwProcessor.single_selected_record).parents('tr').height();
			
			
			var scrollPosition = $element_to_scroll.scrollTop();
			var ref = $element.offset().top;
			scrollTopAmount = $element.scrollTop() + $('#'+ $nwProcessor.single_selected_record).parents('tr').offset().top - ref;
			
			$element
			.scrollTop(scrollPosition + scrollTopAmount);
		}
	},
	
	bind_create_field_selector_control: function(){
		//bind click events
		$("body")
		.on( "click", 'ul#record-details-field-selector input[type="checkbox"]', function(e){
			if( $(this).attr('checked') )
				$( '.details-section-container-row-'+$(this).val() ).show();
			else
				$( '.details-section-container-row-'+$(this).val() ).hide();
		});
	},
	
	create_field_selector_control: function( $context ){
		
		var $details_table = $context.find('table.main-details-table:first');
		
		var list_elements = '';
		
		if( $details_table ){
			$details_table
			.find('tr')
			.each(function(){
				
				list_elements += '<li><label class="checkbox"><input type="checkbox" value="'+$(this).attr('jid')+'" checked="checked" />'+$(this).find('td.details-section-container-label').text()+'</label></li>';
				
			});
			
	 
			// Using DOMPurify to sanitize the content
			var cleanHTML = DOMPurify.sanitize(list_elements);
			$('ul#record-details-field-selector').html(cleanHTML);										  
			/*$('ul#record-details-field-selector')
			.html( list_elements );*/
			
		}
	},
	
	//Bind Multi-select option tooltip
	timer_interval: '',
	mouse_vertical_position: '',
	
	pause_reprocessing: function(){
		//console.log('pause');
		if( $.fn.cProcessForm.returned_ajax_data && Object.getOwnPropertyNames( $.fn.cProcessForm.returned_ajax_data ).length && $.fn.cProcessForm.returned_ajax_data.re_process_code ){
			if( $.fn.cProcessForm.returned_ajax_data.highchart_data )
				delete $.fn.cProcessForm.returned_ajax_data.highchart_data;
			
			if( $.fn.cProcessForm.returned_ajax_data.html )
				delete $.fn.cProcessForm.returned_ajax_data.html;
			
			$(document).data( 're_process', $.fn.cProcessForm.returned_ajax_data );
			$.fn.cProcessForm.returned_ajax_data.re_process = 0;
		}else{
			alert("Could not Pause Processing, due to invalid data");
		}
	},
	
	resume_reprocessing: function(){
		if( $(document).data( 're_process' ) ){
			var data = $(document).data( 're_process' );
			if( data.re_process_code )trigger_new_ajax_request( data );
			$(document).data( 're_process', '' );
		}else{
			alert("Could not Resume Processing");
		}
	},
		
	show_hidden_columns: function(){
		$(".hidden-column")
		.removeClass("hidden-column");
		
		$(".hidden-column-shown")
		.removeClass( $(".hidden-column-shown").attr("old-class") )
		.addClass( $(".hidden-column-shown").attr("new-class") )
		.removeClass("hidden-column-shown");
		
		if( $( "table.activated-table") ){
			
			$( "table.activated-table")
			.dataTable()
			.fnAdjustColumnSizing();
		}
	},
	
	resizeHeight:'',
	resizeWidth:'',
	resizeTimer2:'',
	resize: function(){
		//|| $nwProcessor.resizeHeight != $(document).height() 
		if( $nwProcessor.resizeWidth != $(document).width() ){
			if( $nwProcessor.resizeTimer2 )clearTimeout( $nwProcessor.resizeTimer2 );
			$nwProcessor.resizeTimer2 = setTimeout(function() {
			  if( $( "table.activated-table").is(":visible") ){
					$nwProcessor.resizeWidth = $(document).width(); 
					$nwProcessor.resizeHeight = $(document).height();
			
					$( "table.activated-table")
					.dataTable()
					.fnAdjustColumnSizing();
					$nwProcessor.update_column_view_state();
				}
			}, 250);
		}
		if( ! $nwProcessor.resizeWidth ){
			$nwProcessor.resizeWidth = $(document).width(); 
			$nwProcessor.resizeHeight = $(document).height();
		}
		
	},
	
	}
}(jQuery));

$nwProcessor.get_properties_of_school();
$nwProcessor.bind_show_hide_column_checkbox();
//$nwProcessor.initiate_tiny_mce_for_popup_textarea( 'textarea#popTextArea' );
$nwProcessor.bind_create_field_selector_control();

window.addEventListener("resize", function() {
	$nwProcessor.resize();
});

$( document ).ready(function(){
	var isOverIFrame = false;
	$('body')
	.on('mouseout', 'iframe[name="help"]', function(e){
		$(document).scrollTop(0);
	});
});
