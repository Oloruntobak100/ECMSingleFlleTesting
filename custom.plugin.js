$.fn.modal.Constructor.prototype.enforceFocus = function () {};

(function($) {
	$.fn.cProcessForm = {
		api:0,
		api_get_params:'',
		customURL: "",
		requestURL: "",
		activateAjaxRequestButton: function(){
			if( $.fn.cProcessFormUrl && $.fn.cProcessFormUrl.requestURL ){
				$.fn.cProcessForm.requestURL = $.fn.cProcessFormUrl.requestURL;
				if( $.fn.cProcessFormUrl.customURL ){
					$.fn.cProcessForm.customURL = 1;
				}
			}

			$.fn.cProcessForm.bind_quick_print_function();

			$("body")
				.on( "click", ".ajax-request", function(e){
					e.preventDefault();

					if( $(this).hasClass("ajax-request-modals") ){
						var safeHref = sanitizeUrl($(this).attr("data-href"));
						if (safeHref) {
							$(this).attr("href", safeHref);
						} else {
							console.error("Invalid URL detected in data-href.");
						}
					}

					var data_id = ( $(this).attr("data-id") )?$(this).attr("data-id"):"";
					var data_filter = ( $(this).attr("data-filter") )?$(this).attr("data-filter"):"";
					var data_internalcard = ( $(this).attr("data-internalcard") )?$(this).attr("data-internalcard"):"";

					$.fn.cProcessForm.ajax_data = {
						ajax_data: {filter: data_filter, id:data_id , internalcard:data_internalcard },
						form_method: 'post',
						ajax_data_type: 'json',
						ajax_action: 'request_function_output',
						ajax_container: '',
						ajax_get_url: "?action=" + $(this).attr("action") + "&todo=" + $(this).attr("todo"),
					};
					$.fn.cProcessForm.ajax_send();

				});

			$("body")
				.on( "click", "input.select-all-checkbox", function(e){
					var child = $(this).attr("data-children");

					if( $(this).is(":checked") ){
						$('input.' + child).prop("checked", true );
					}else{
						$('input.' + child).prop("checked", false );
					}
				});

			$("body")
				.on( "click", ".custom-action-button", function(e){
					e.preventDefault();

					var $me = $(this);
					if( $me.hasClass("activated-click-event") ){
						return false;
					}

					var store = "";
					if( $('#current-store-container').find("select") && $('#current-store-container').find("select").val() ){
						store = $('#current-store-container').find("select").val();
					}

					var function_id = $me.attr('function-id');
					var function_name = $me.attr('function-name');
					var function_class = $me.attr('function-class');

					if( ! $(this).attr("skip-title") )
						$( "#active-menu-text" ).html( function_id );

					var budget_id = '';
					var month_id = '';
					var operator_id = '';
					var department_id = '';
					var start_date = '';
					var end_date = '';

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

					var module_id = "";
					$.fn.cProcessForm.ajax_data = {
						ajax_data: {action:function_class, todo:function_name, module:module_id, id:function_id, budget:budget_id, month:month_id, department:department_id, operator:operator_id, store:store, end_date:end_date, start_date:start_date },
						form_method: 'get',
						ajax_data_type: 'json',
						ajax_action: 'request_function_output',
						ajax_container: '',
						ajax_get_url: "?",
					};
					$.fn.cProcessForm.ajax_send();

				});

			$("body")
				.on( "click", ".custom-single-selected-record-button", function(e){
					if( ! $(this).attr('allow-default') ){
						e.preventDefault();
					}

					var single_selected_record = "";

					var store = "";
					if( $('#current-store-container').find("select") && $('#current-store-container').find("select").val() ){
						store = $('#current-store-container').find("select").val();
					}
					//$.fn.cProcessForm.activateProcessing( $(this) );

					if( $(this).attr("override-selected-record") ){
						single_selected_record = $(this).attr("override-selected-record");
					}

					if( ( ! single_selected_record  ) && $(this).attr("selected-record") ){
						single_selected_record = $(this).attr("selected-record");
					}

					var ids = '';
					if( $(this).attr('use-checkbox') ){
						ids = $('input.'+ $(this).attr('use-checkbox') +':checked').map(function() {return this.value;}).get().join(':::');

						if( ! ids ){
							var data = {theme:'alert-info', err:'No Record Selected', msg:'You must select items by clicking on the checkboxes first', typ:'jsuerror' };
							$.fn.cProcessForm.display_notification( data );
							return false;
						}

					}

					if( single_selected_record || ids ){
						var ok = 1;
						if( $(this).attr('confirm-prompt') ){
							ok = confirm( 'Are you sure that you want to ' + $(this).attr('confirm-prompt') );
						}

						if( ok ){
							var module_id = "";
							var url = $(this).attr('action');
							if( ! $(this).attr('no-store') ){
								url += "&store=" + store;
							}
							$.fn.cProcessForm.ajax_data = {
								ajax_data: {mod:$(this).attr('mod'), id:single_selected_record, ids:ids},
								form_method: 'post',
								ajax_data_type: 'json',
								ajax_action: 'request_function_output',
								ajax_container: '',
								ajax_get_url: url,
							};
							if( single_selected_record == "json" ){
								$.fn.cProcessForm.ajax_data.ajax_data.json = $("body").data("json");
							}
							$.fn.cProcessForm.ajax_send();

							if( $(this).hasClass("one-time-request") ){
								$(this).attr('action', '');
								$(this).removeClass('custom-single-selected-record-button');
							}
						}

					}else{
						var data = {theme:'alert-info', err:'No Selected Record', msg:'Please select a record by clicking on it', typ:'jsuerror' };
						$.fn.cProcessForm.display_notification( data );
					}

				});

			$('body')
				.on("click", 'li.app-sidebar__heading', function( e ){

					$(this)
						.siblings()
						.find("i")
						.removeClass("icon-caret-down")
						.addClass("icon-caret-left");

					$(this)
						.siblings()
						.next()
						.find("ul")
						.addClass("hidden");

					$(this)
						.find("i")
						.removeClass("icon-caret-left")
						.addClass("icon-caret-down");

					$(this)
						.next()
						.find("ul:first")
						.hide()
						.removeClass("hidden")
						.slideDown();

				});
		},
		activateTabandMenu: function(){
			if( $('#test-mode-container').is(":visible") ){
				setInterval( function(){
					$('#test-mode-container').toggle();
				}, 1000 );
			}

			$('ul#main-tabs')
				.find("a.nav-link")
				.on("click", function( e ){

					$('ul#main-tabs')
						.find("a.nav-link")
						.removeClass("active");

					$(this)
						.addClass("active");

				});

			$('button.mobile-toggle-header-nav')
				.on("click", function( e ){
					if( $('ul#main-tabs').is(":visible") ){
						$('.app-header__content')
							.add('.app-header-left')
							.css("visibility", "hidden")
							.css("opacity", 0);

						$('.app-header__content')
							.css("top", "0");
						//.css("position", "relative")

						$('ul#main-tabs')
							.hide();
					}else{
						$('.app-header__content')
							.add('.app-header-left')
							.css("visibility", "visible")
							.css("opacity", 1);

						//.css("position", "relative")

						$('ul#main-tabs')
							.show();

						$('.app-header__content')
							.css("top", "70px")
							.css("height", "auto")
							.css("width", "100%")
							.css("left", "0");
					}
				});

			$('button.close-sidebar-btn')
				.add('button.mobile-toggle-nav')
				.add('button.mobile-toggle-header-na')
				.on("click", function( e ){

					if( $('.app-sidebar').css('transform') == "none" ){
						$('.fixed-sidebar .app-main .app-main__outer').css('padding-left', "0px");
						$('.app-sidebar').css('transform', "translateX(-280px)");
					}else{
						$('.fixed-sidebar .app-main .app-main__outer').css('padding-left', "280px");
						$('.app-sidebar').css('transform', "none");
					}

					$nwProcessor.reload_datatable();
				});
		},
		confirmPrompt: function( id ){
			var reason = '';

			$me = $( "#" + id );

			var rlabel = $me.attr("data-reason");
			if( ! rlabel )rlabel = 'Reason';

			var reason = prompt( rlabel );
			if( ! reason ){
				var data = {theme:'alert-danger', err:'No Reason Specified', msg:'Please specify a reason', typ:'jsuerror' };
				$.fn.cProcessForm.display_notification( data );
				return false;
			}

			var store = "";
			if( $('#current-store-container').find("select") && $('#current-store-container').find("select").val() ){
				store = $('#current-store-container').find("select").val();
			}

			var function_id = $me.attr('function-id');
			var function_name = $me.attr('function-name');
			var function_class = $me.attr('function-class');

			if( ! $(this).attr("skip-title") )
				$( "#active-menu-text" ).html( function_id );

			var budget_id = '';
			var month_id = '';
			var operator_id = '';
			var department_id = '';
			var start_date = '';
			var end_date = '';

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

			var module_id = "";
			$.fn.cProcessForm.ajax_data = {
				ajax_data: {action:function_class, todo:function_name, module:module_id, id:function_id, budget:budget_id, month:month_id, department:department_id, operator:operator_id, store:store, end_date:end_date, start_date:start_date, reason:reason },
				form_method: 'get',
				ajax_data_type: 'json',
				ajax_action: 'request_function_output',
				ajax_container: '',
				ajax_get_url: "?",
			};
			$.fn.cProcessForm.ajax_send();

		},
		handleSubmission: function( $form ){
			$form.on('submit', function(e){
				e.preventDefault();
				var d = $.fn.cProcessForm.transformData( $(this) );

				if( d.error ){
					var settings = {
						message_title:d.title,
						message_message: d.message,
						auto_close: 'no'
					};
					display_popup_notice( settings );
				}else{
					var local_store = 0;
					internetConnection = true;

					d[ 'object' ] = $(this).attr('name');

					if( $(this).attr('local-storage') ){
						local_store = 1;

						//store data
						//var stored = store_record( data );
						//successful_submit_action( stored );

						alert('local storage');
					}

					if( ! local_store ){
						$(this).data('do-not-submit', 'submit' )
						$.fn.cProcessForm.post_form_data( $(this) );

						tempData = d;
					}

					$form
						.find('input')
						.not('.do-not-clear')
						.val('');
				}
				return d;
			});
		},
		transformData: function( $form ){

			var data = $form.serializeArray();

			var error = {};
			var txData = { error:false };
			var unfocused = true;

			$.each( data , function( key , value ){
				var $input = $form.find('#'+value.name+'-field');
				if( $input ){
					if( $input.attr('data-validate') ){
						var validated = $.fn.cProcessForm.validate.call( $input , unfocused );

						if( ! ( error.error ) && validated.error ){
							//throw error & display message
							error = validated;
							unfocused = false;
						}else{
							//start storing object
							txData[ value.name ] = value.value;
						}

					}else{
						txData[ value.name ] = value.value;
					}
				}
			});

			if( error.error ){
				return error;
			}

			return txData;
		},
		ajax_data: {},
		returned_ajax_data: {},
		activateProcessing: function( $button ){
			if( ! $button.hasClass("processing-ajax-request") ){
				$button
					.addClass("processing-ajax-request");

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
			if( $(".processing-ajax-request") ){
				$button = $(".processing-ajax-request");

				if( $button.attr("type") && $button.attr("type") == "submit" ){
					$button
						.css( "opacity", 1 )
						.val( $button.attr("data-tmp") );
				}else{
					$button
						.css( "opacity", 1 )
						.text( $button.attr("data-tmp") );
				}
				$button.removeClass("processing-ajax-request");
			}
		},
		post_form_data: function( $form ){

			if( $form.data('do-not-submit') != 'submit' ){
				return false;
			}

			$form
				.find('.nw-database_objects')
				.each(function(){

					var d = {};
					var df = {};
					var $textarea = $(this).find('textarea.nw-database_objects-store');

					var df1 = JSON.parse( $textarea.val() );
					if( df1["data"] ){
						df = df1["data"];
					}
					/*
                    if( df1["values"] ){
                        d = df1["values"];
                    }
                    */

					$(this).find('.form-gen-element')
						.each(function(){

							var v = $(this).val();

							if( ! d[ $(this).attr('data-nid') ] ){
								d[ $(this).attr('data-nid') ] = {};
							}

							if( ! d[ $(this).attr('data-nid') ][ $(this).attr('data-nfield') ] ){
								d[ $(this).attr('data-nid') ][ $(this).attr('data-nfield') ] = {};
							}

							if( ! d[ $(this).attr('data-nid') ][ $(this).attr('data-nfield') ][ $(this).attr('data-nkey') ] ){
								d[ $(this).attr('data-nid') ][ $(this).attr('data-nfield') ][ $(this).attr('data-nkey') ] = {};
							}

							d[ $(this).attr('data-nid') ][ $(this).attr('data-nfield') ][ $(this).attr('data-nkey') ][ $(this).attr('data-key') ] = v;
						});


					var d1 = {
						data: df,
						values: d,
					};
					//console.log(d1);

					$textarea.val( JSON.stringify( d1 ) );
				});


			$.fn.cProcessForm.ajax_data = {
				ajax_data: $form.serialize(),
				form_method: 'post',
				ajax_data_type: 'json',
				ajax_action: 'request_function_output',
				ajax_container: '',
				ajax_get_url: $form.attr('action'),
			};
			$.fn.cProcessForm.ajax_send();
		},
		cloneTable:function( id, action ){
			if( id ){
				var $table = $("#nw-object-" + id );
				var cText = $('textarea[name="'+ $table.attr('data-field') +'"]').val();

				var todo = 'clone_database_object';

				switch( action ){
					case "remove":
						var todo = 'remove_database_object';
						break;
				}

				$.fn.cProcessForm.ajax_data = {
					ajax_data: { current_value: cText, data_field:$table.attr('data-field'), data_nfield:$table.attr('data-nfield'), data_nid:$table.attr('data-nid') },
					form_method: 'post',
					ajax_data_type: 'json',
					ajax_action: 'request_function_output',
					ajax_container: '',
					ajax_get_url: "?action=database_objects&todo=" + todo,
				};
				$.fn.cProcessForm.ajax_send();
			}
		},
		function_click_process: 1,
		callbacks: [],
		ajax_send: function( settings ){
			//Send Data to Server

			if( $.fn.cProcessForm.function_click_process ){

				if( $("body").hasClass("modal-open") ){
					if( $.fn.cProcessForm.ajax_data.ajax_get_url )$.fn.cProcessForm.ajax_data.ajax_get_url += "&modal=1";
					else $.fn.cProcessForm.ajax_data.ajax_get_url = "?modal=1";
				}

				var url = $.fn.cProcessForm.requestURL + 'Endpoint' + $.fn.cProcessForm.ajax_data.ajax_get_url;
				//var url = 'http://localhost:8080/ECM/
				// ' + $.fn.cProcessForm.ajax_data.ajax_get_url;

				if( $.fn.cProcessForm.customURL ){
					url = $.fn.cProcessForm.requestURL + $.fn.cProcessForm.ajax_data.ajax_get_url;
				}else if( $.fn.cProcessForm.api ){
					url = $.fn.cProcessForm.requestURL + 'api/' + $.fn.cProcessForm.ajax_data.ajax_get_url;
					if( $.fn.cProcessForm.api_get_params ){
						url += $.fn.cProcessForm.api_get_params;
					}
				}

				$.ajax({
					dataType: $.fn.cProcessForm.ajax_data.ajax_data_type,
					type:$.fn.cProcessForm.ajax_data.form_method,
					data:$.fn.cProcessForm.ajax_data.ajax_data,
					crossDomain:true,
					url: url,
					timeout:120000,
					beforeSend:function(){
						$.fn.cProcessForm.function_click_process = 0;
						$('div#generate-report-progress-bar')
							.html('<div class="virtual-progress-bar progress progress-striped"><div class="progress-bar progress-bar-info"></div></div>');

						$.fn.cProcessForm.progress_bar_change.call();
					},
					error: function(event, request, settings, ex) {
						$.fn.cProcessForm.function_click_process = 1;
						$.fn.cProcessForm.requestRetryCount = 0;

						$.fn.cProcessForm.ajaxError.call( event, request, settings, ex );

						$.fn.cProcessForm.deactivateProcessing();
					},
					success: function(data){
						$.fn.cProcessForm.requestRetryCount = 0;
						$.fn.cProcessForm.function_click_process = 1;
						$.fn.cProcessForm.deactivateProcessing();

						if( data && Object.getOwnPropertyNames( data ).length && typeof data === 'object' && data.status ){

							$.fn.cProcessForm.returned_ajax_data = data;

							if( data.api_get_params ){
								$.fn.cProcessForm.api_get_params = data.api_get_params;
							}

							switch(data.status){
								case 'authenticated-visitor':
									data.url = $.fn.cProcessForm.requestURL;
									authenticated_visitor( data );
									return;
									break;
								case 'got-recent-activities':
									data.url = $.fn.cProcessForm.requestURL;
									got_recent_activities( data );
									return;
									break;
								case "column-toggle":
									$nwProcessor.ajax_hide_show_column_checkbox( data );
									$nwProcessor.bind_details_view_control();
									break;
								case "new-status":
									if( data ){
										if( data.log_user_id && data.data && data.data.user_data && data.data.user_data.id ){
											$.fn.cProcessForm.localStore( data.log_user_id, data.data.user_data.id, {}, 'put' )
										}

										if( data.redirect_url ){
											document.location = data.redirect_url;
										}

										if( data.html ){
											$('#main-view')
												.html( data.html );
										}

										if( data.html_add_class_selector && data.html_add_class ){
											$(data.html_add_class_selector)
												.addClass( data.html_add_class );
										}

										if( data.html_replacement_selector && data.html_replacement ){
											$(data.html_replacement_selector)
												.html( data.html_replacement );
										}

										if( data.html_replacement_selector_one && data.html_replacement_one ){
											$(data.html_replacement_selector_one)
												.html( data.html_replacement_one );
										}

										if( data.html_replacement_selector_two && data.html_replacement_two ){
											$(data.html_replacement_selector_two)
												.html( data.html_replacement_two );
										}

										if( data.html_replacement_selector_three && data.html_replacement_three ){
											$( data.html_replacement_selector_three )
												.html( data.html_replacement_three );
										}

										if( data.html_prepend_selector && data.html_prepend ){
											$(data.html_prepend_selector)
												.prepend( data.html_prepend );
										}

										if( data.html_prepend_selector_one && data.html_prepend_one ){
											$(data.html_prepend_selector_one)
												.prepend( data.html_prepend_one );
										}

										if( data.html_append_selector && data.html_append ){
											$(data.html_append_selector)
												.append( data.html_append );
										}

										if( data.html_append_selector_one && data.html_append_one ){
											$(data.html_append_selector_one)
												.append( data.html_append_one );
										}

										if( data.html_replace_selector && data.html_replace ){
											$(data.html_replace_selector)
												.replaceWith( $(data.html_replace) );
										}

										if( data.html_removal ){
											if( $(data.html_removal) )$(data.html_removal).remove();
										}

										if( data.html_removals ){
											$.each( data.html_removals , function( key, value ){
												if( $( value ) )$( value ).remove();
											} );
										}

										var cc = [];
										if( data.javascript_functions && data.javascript_functions.length > 0 ){
											cc = data.javascript_functions;
										}

										if( $.fn.cProcessForm.callbacks.length > 0 ){
											cc = cc.concat( $.fn.cProcessForm.callbacks );
										}

										if( cc.length > 0 ){
											tmp_data = data;
											//console.log("tg", tmp_data);
											$.each( cc , function( key, value ){
												if( value )eval( value + "()" );
											} );

										}

										if(data.saved_record_id){
											single_selected_record = data.saved_record_id;
										}

									}
									break;
								case "reload-page":
									document.location = document.location;
									break;
								case "got-quick-details-view":
								case "display-appsettings-setup-page":
								case "display-data-capture-form":
								case "display-advance-search-form":
								case "modify-appsettings-settings":
								//case "redirect-to-dashboard":
								case "redirect-to-login":
								case "displayed-dashboard":
								case "deleted-records":
								case "column-toggle":
								case "reload-datatable":
								case "display-datatable":
								case "saved-form-data":
								case "download-report":
									if( typeof ( $nwProcessor ) !== 'undefined' ){
										return $nwProcessor.ajax_request_function_output( data );
									}
									break;
							}

							if( data.development ){
								$.fn.cProcessForm.activateDevelopmentMode();
							}

						}else{
							if( ! ( data && Object.getOwnPropertyNames( data ).length && typeof data === 'object' && data.typ ) ){
								var data = {theme:'alert-danger', err:'Unsuccessful Request', msg:'Please check your Network Connection & Try Again', typ:'jsuerror' };
							}
						}


						if( data && data.notification ){
							//console.log( data.notification );
						}

						$.fn.cProcessForm.display_notification( data );
					}
				});
			}
		},
		cancelAjaxRecursiveFunction:0,
		activateDevelopmentMode: function(){
			$('.custom-action-button-old')
				.add('.custom-multi-selected-record-button')
				.add('.custom-single-selected-record-button')
				.add('.custom-single-selected-record-button-old')
				.add('.custom-action-button')
				.not(".activatedRightClick")
				.on("contextmenu", function( event ){
					if( event.ctrlKey ){
						event.stopPropagation();

						var _class = '';
						var _method = '';

						var _class1 = '';
						var _method1 = '';
						if( $(this).hasClass('custom-action-button-old') || $(this).hasClass('custom-action-button') ){
							_method = $(this).attr("function-name");
							_class = $(this).attr("function-class");
						}else{
							if( $(this).attr("action") ){
								var cl = $(this).attr("action").replace('?', '');
								if( cl ){
									var cl2 = cl.split('&');
									for( var i = 0; i < cl2.length; i++){
										var cl3 = cl2[ i ].split('=');
										if( cl3[0] == 'action' && cl3[1] ){
											_class = cl3[1];
										}
										if( cl3[0] == 'nwp_action' && cl3[1] ){
											_class1 = cl3[1];
										}
										if( cl3[0] == 'todo' && cl3[1] ){
											_method = cl3[1];
										}
										if( cl3[0] == 'nwp_todo' && cl3[1] ){
											_method1 = cl3[1];
										}
									}
								}
							}
						}


						if( _class ){
							var pc = _class;
							var pm = _method;
							var yx = { class: _class, method: _method };

							if( _method1 && _class1 ){
								pc += '.' + _class1;
								pm = _method1;

								yx.nwp_action = _class1;
								yx.nwp_todo = _method1;
							}

							var y = prompt("Open Class: " + pc, pm );
							if( y ){
								$.fn.cProcessForm.ajax_data = {
									ajax_data: yx,
									form_method: 'post',
									ajax_data_type: 'json',
									ajax_action: 'request_function_output',
									ajax_container: '',
									ajax_get_url: "?action=database_table&todo=open_class",
								};
								$.fn.cProcessForm.ajax_send();
							}
						}
					}
				})
				.addClass("activatedRightClick");

			$(".hyella-source-container")
				.not(".activated")
				.on("contextmenu", function( event ){
					event.stopPropagation();
					var y = prompt("Open File Directory", $(this).attr("nwp-file") );
					if( y ){
						/*
                        * @todo: open file in IDE or editor
                        * */
						/*$.fn.cProcessForm.ajax_data = {
                            ajax_data: {file: $(this).attr("nwp-file") },
                            form_method: 'post',
                            ajax_data_type: 'json',
                            ajax_action: 'request_function_output',
                            ajax_container: '',
                            ajax_get_url: "?action=database_table&todo=open_file",
                        };
                        $.fn.cProcessForm.ajax_send();*/
					}
				})
				.addClass("activated");

		},
		triggerNewAjaxRequest: function(){
			$.fn.cProcessForm.ajax_data = {
				ajax_data: {mod: $.fn.cProcessForm.returned_ajax_data.mod, id:$.fn.cProcessForm.returned_ajax_data.id },
				form_method: 'post',
				ajax_data_type: 'json',
				ajax_action: 'request_function_output',
				ajax_container: '',
				ajax_get_url: $.fn.cProcessForm.returned_ajax_data.action,
			};
			$.fn.cProcessForm.ajax_send();
		},
		ajaxError: function( event, request, settings, ex ){
			var data = {theme:'alert-danger', err:'Response Error', msg:'Please try again or contact support team', typ:'jsuerror' };
			$.fn.cProcessForm.display_notification( data );
		},
		activate_highcharts: function(){
			var tmp_data = $.fn.cProcessForm.returned_ajax_data;
			if( tmp_data && Object.getOwnPropertyNames( tmp_data ).length && tmp_data.highchart_data && tmp_data.highchart_container_selector ){
				nwHighCharts.initChart( tmp_data );
			}else{
				alert("Could not Generate Chart, due to invalid data");
			}
		},
		activate_and_export_highcharts: function(){
			var tmp_data = $.fn.cProcessForm.returned_ajax_data;
			if( tmp_data && Object.getOwnPropertyNames( tmp_data ).length && tmp_data.highchart_data && tmp_data.highchart_container_selector ){
				var dataString = nwHighCharts.initChartAndExport( tmp_data );

				$.ajax({
					type: 'POST',
					data: dataString,
					url:  $.fn.cProcessForm.requestURL + 'classes/highcharts/exporting-server/php/php-batik/',
					success: function( data ){
						//console.log( data );
						resume_reprocessing();
					}
				});
			}else{
				alert("Could not Generate Chart, due to invalid data");
			}
		},
		activateAjaxForm: function(){

			//Bind Html text-editor
			$.fn.cProcessForm.activateFullTextEditor();

			//Activate Client Side Validation / Tooltips
			$.fn.cProcessForm.activateTooltip();

			//Bind Form Submit Event
			$('form.activate-ajax')
				.not('.ajax-activated')
				.bind('submit', function( e ){
					e.preventDefault();

					$.fn.cProcessForm.activateFormValidation( $(this) );
					if( $(this).data('do-not-submit') != 'submit' ){
						return false;
					}

					if( $(this).hasClass("confirm-prompt") ){
						var c = confirm("Are you sure you want to submit this form?");
						if( ! c ){
							return false;
						}
					}

					$.fn.cProcessForm.activateProcessing( $(this).find("input[type='submit']") );
					$.fn.cProcessForm.post_form_data( $(this) );
				})
				.find('input[required="required"]')
				.each(function(){
					$(this).addClass('form-element-required-field');
				});

			$('form.activate-ajax.nw-track-changes')
				.not('.ajax-activated')
				.find('input,select,textarea')
				.change( function(){
					if( ! $(this).parents('form').hasClass('nw-has-changes') ){
						$(this).parents('form').addClass('nw-has-changes');
						$(this).parents('form').removeClass('nw-track-changes');
					}
				});

			$('form.activate-ajax')
				.not('.ajax-activated')
				.find("select.onchange")
				.on("change", function(){
					if( $(this).val() && $(this).attr('action') ){
						var trecord_id = $(this).parents('form').find('input[name="id"]').val();
						var ttable = $(this).parents('form').find('input[name="table"]').val();

						$.fn.cProcessForm.ajax_data = {
							ajax_data: { id:$(this).val(), mod:trecord_id, table:ttable },
							form_method: 'post',
							ajax_data_type: 'json',
							ajax_action: 'request_function_output',
							ajax_container: '',
							ajax_get_url: $(this).attr('action'),
						};
						$.fn.cProcessForm.ajax_send();

					}
				})
				.change();

			$('form.activate-ajax')
				.not('.ajax-activated')
				.find(".onchange2")
				.on("change", function(){
					if( $(this).val() && $(this).attr('action2') ){
						var trecord_id = $(this).parents('form').find('input[name="id"]').val();
						var ttable = $(this).parents('form').find('input[name="table"]').val();

						$.fn.cProcessForm.ajax_data = {
							ajax_data: { id:$(this).val(), mod:trecord_id, table:ttable },
							form_method: 'post',
							ajax_data_type: 'json',
							ajax_action: 'request_function_output',
							ajax_container: '',
							ajax_get_url: $(this).attr('action2'),
						};
						$.fn.cProcessForm.ajax_send();

					}
				})
				.change();

			//Activate Ajax file upload
			$.fn.cProcessForm.ajaxFileUploader();

			/*if ( jQuery().datepicker) {
               var FromEndDate = new Date();

               $('input[type="date"]')
               .not(".limit-date")
               .not(".limit-date2")
               .not(".active")
               .datepicker({
                   rtl: App.isRTL(),
                   autoclose: true,
                   format: 'yyyy-mm-dd',
               })
               .addClass("active");

               $('input.limit-date')
               .not(".active")
               .datepicker({
                   rtl: App.isRTL(),
                   autoclose: true,
                   format: 'yyyy-mm-dd',
                   endDate: FromEndDate,
               })
               .addClass("active");

               $('input.limit-date2')
               .not(".active")
               .datepicker({
                   rtl: App.isRTL(),
                   autoclose: true,
                   format: 'yyyy-mm-dd',
                   startDate: FromEndDate,
               })
               .addClass("active");


               $('body').removeClass("modal-open"); // fix bug when inline picker is used in modal
           }*/

			//$('form.activate-ajax')
			$('select.select2')
				.not(".active")
				.select2()
				.addClass("active");

			//$('form.activate-ajax')
			$('input.select2')
				.not(".active")
				.each(function(){
					if( $(this).hasClass("allow-clear") ){
						$('<span class="pull-right" style="position: relative;"><a href="#" ref="input#'+$(this).attr("id")+'" class="clear-select2"><small>clear</small></a></span>')
							.insertBefore(this);

						$(this).parent().css('display', 'initial');
					}

					var action = $(this).attr("action");
					var minlength = 2;
					if( $(this).attr("minlength") )minlength = parseFloat( $(this).attr("minlength") * 1 );
					if( isNaN( minlength ) )minlength = 2;

					var form_id = '';
					var form_table_id = '';

					var $form = $(this).parents('form');
					if( $form && $form.find('input[name="id"]') ){
						form_id = $form.find('input[name="id"]').val();
						form_table = $form.find('input[name="table"]').val();
						form_table_id = $form.find('input[name="table_id"]').val();

						if( form_id ){
							action = action + '&form_id=' + form_id;
						}

						if( form_table ){
							action = action + '&form_table=' + form_table;
						}

						if( form_table_id ){
							action = action + '&form_table_id=' + form_table_id;
						}
					}

					//var surl = $.fn.cProcessForm.requestURL +'php/ajax_request_processing_script.php' + action;
					action = action.replace("?action=", "?action=get_select2&nwp2_action=");
					var surl = $.fn.cProcessForm.requestURL + 'Endpoint' + action;

					if( $.fn.cProcessForm.customURL ){
						surl = $.fn.cProcessForm.requestURL + action;
					}

					var gData = {
						ajax: {
							url: surl,
							dataType: 'json',
							delay: 250,
							type: "post",
							data: function ( term, page ) {
								var d = { term: term, page : page, page_limit: 2 };

								var $p = $(this).attr("data-params");
								if( $p && $(this).parents('form').find( $p ) ){
									$(this).parents('form').find( $p ).each( function(){

										if( $(this).attr("field_key") ){
											d[ $(this).attr("field_key") ] = $(this).val();
										}else{
											d[ $(this).attr("name") ] = $(this).val();
										}

									} );
								}

								return d;
							},
							results: function (data, page) { // parse the results into the format expected by Select2.
								// since we are using custom formatting functions we do not need to alter remote JSON data
								return {
									results: data.items
								};
							},
							cache: true
						},
						allowClear: true,
						minimumInputLength: minlength,
						initSelection: function (element, callback) {

							if( element.attr('tags') &&  element.attr('tags') == "true" ){
								if( $('textarea#' + element.attr('id') + '-option-array' ) ){
									var val = $('textarea#' + element.attr('id') + '-option-array' ).val();
									if( val ){
										//element.val('');

										var j = JSON.parse( val );
										if( ! $.isEmptyObject( j ) ){
											var a = [];

											$.each( j, function( k1, v1 ){
												a.push({ id:k1, text:v1 });
											} );

											callback( a );
										}
									}
								}
							}else{
								if( element.val() &&  element.attr('label') ){
									//var dv = element.val().split(",");
									//var dl = element.attr('label').split(",");
									callback({ id: element.val(), text: element.attr('label') });
								}
							}

						},
					};

					if( $(this).attr("tags") && $(this).attr("tags") == "true" ){
						gData.tags = true;
					}

					if( $(this).attr("data-format") ){
						gData.formatResult = $.fn.cProcessForm.repoFormatResult;
					}

					$(this)
						.select2( gData );
				})
				.addClass("active");

			$("a.clear-select2")
				.on("click", function(e){
					e.preventDefault();
					if( $(this).attr("ref") ){
						$(this).parents("form").find( $(this).attr("ref") ).select2("val", "");
					}
				});

			$.fn.cProcessForm.activateAutocomplete();
			$('form.activate-ajax').addClass('ajax-activated');

		},
		repoFormatResult: function(repo) {
			var markup = '<div class="row">' +
				'<div class="col-md-12"><b>' + repo.text + '</b></div>';

			if( repo.available ){
				markup += '<div class="col-md-12"><small style="color:green;">' + repo.available + '</small></div>';
			}

			markup += '</div><br />';

			//markup += '</div></div>';

			return markup;
		},
		repoFormatSelection: function (repo) {
			return repo.full_name;
		},
		activateAutocomplete: function(){
			if( $('.awesomcomplete-input') && $('.awesomcomplete-input').not(".ajax-activated") ){
				$('.awesomcomplete-input').each( function(){
					var input_id = $(this).attr("id");
					var input = document.getElementById( input_id );

					new Awesomplete( input, {
						filter: function(text, input) {
							return Awesomplete.FILTER_CONTAINS(text, input.match(/[^,]*$/)[0]);
						},

						item: function(text, input) {
							return Awesomplete.ITEM(text, input.match(/[^,]*$/)[0]);
						},

						replace: function(text) {
							var before = this.input.value.match(/^.+,\s*|/)[0];
							this.input.value = before + text + ", ";
						}
					});

					$(this).addClass('ajax-activated');
				});
			}
		},
		activateFullTextEditor: function(){
			$('#myModal')
				.not(".modal-key-down-bind")
				.on('show.bs.modal', function(){
					tinyMCE.activeEditor.setContent( nwResizeWindow.editingTextarea.val() );
				})
				.on('hide.bs.modal', function(){
					nwResizeWindow.editingTextarea
						.val( $("#popTextArea_ifr").contents().find("body").html() );
				})
				.addClass("modal-key-down-bind");

			$('textarea')
				.not( '.activated' )
				.bind('keydown', function(e){

					switch(e.keyCode){
						case 69:	//E Ctrl [17]
							if(e.ctrlKey){
								e.preventDefault();

								nwResizeWindow.editingTextarea = $(this);

								//Set Contents
								$('#myModal')
									.modal('show');

								$(this).attr('tip', '');
								$.fn.cProcessForm.displayTooltip.call($(this), '');
							}
							break;
					}

				})
				.bind('focus', function(){
					$(this).attr('tip', 'Press Ctrl+E to display full text editor');

					$.fn.cProcessForm.displayTooltip.call($(this), '');
				})
				.bind('blur', function(){
					$(this).attr('tip', '');

					$.fn.cProcessForm.displayTooltip.call($(this), '');
				})
				.addClass( 'activated' );
		},
		ajaxFileUploader: function(){
			if($('.upload-box').hasClass('cell-element')){

				$('.upload-box')
					.not('.upload-activated')
					.each(function(){
						var id = $(this).attr('id');
						var name = $(this).find('input').attr('name');
						var label = $(this).find('input').attr('label');

						var max_size = parseFloat( $(this).find('input').attr('max-size') );
						if( isNaN( max_size ) )max_size = 0;
						var acceptable_files_format = $(this).find('input').attr('acceptable-files-format');
						var table = $("#"+id).parents('form').find('input[name="table"]').val();
						var form_id = $("#"+id).parents('form').find('input[name="processing"]').val();
						var form_record_id = $("#"+id).parents('form').find('input[name="id"]').val();
						var actual_form_id = $("#"+id).parents('form').attr('id');

						//instead of sending processing id | rather send record id
						if( form_record_id && form_record_id.length > 1 )form_id = form_record_id;

						var name_field = '';
						if( $(this).find('input').attr('data-name-field') ){
							name_field = $(this).find('input').attr('data-name-field');
						}


						var vdirectory = '';
						if( $(this).find('input').attr('directory') ){
							vdirectory = $(this).find('input').attr('directory');
						}
						var vmultiple = false;
						if( $(this).find('input').attr('multiple') ){
							vmultiple = true;
						}
						vmultiple = false;

						$("."+name+"-replace").attr( 'data-value' , $(this).find('input').attr('data-value') );
						$("."+name+"-replace").attr( 'hide_on_select' , $(this).find('input').attr('hide_on_select') );
						$("."+name+"-replace").attr( 'name' , $(this).find('input').attr('name') );
						//$("."+name+"-replace").attr( 'id' , $(this).find('input').attr('id') );
						$("."+name+"-replace").attr( 'class' , $(this).find('input').attr('class') );
						$("."+name+"-replace").attr( 'alt' , $(this).find('input').attr('alt') );

						//var surl = $.fn.cProcessForm.requestURL + 'php/upload.php';
						var surl = $.fn.cProcessForm.requestURL + 'Endpoint' + '?action=upload';


						var uploader = new qq.FileUploader({
							element: document.getElementById(id),
							directory:vdirectory,
							multiple:vmultiple,
							listElement: document.getElementById('separate-list'),
							action: surl,
							params: {
								label: label,
								tableID: table,
								formID: form_id,
								name:name,
								actualFormID:actual_form_id,
								max_size:max_size,
								acceptable_files_format:acceptable_files_format,
							},
							onComplete: function(id, fileName, responseJSON){
								if(responseJSON.success=='true'){
									var did = $('input[name="'+responseJSON.element+'"]').attr( "data-id" );

									$('#' + did )
										.find('.qq-upload-success').remove();
									/*
                                    $('#' + did )
                                    .find('.qq-upload-success')
                                    .find('.qq-upload-failed-text')
                                    .text('Success')
                                    .css('color','#ff6600');
                                    */

									if( responseJSON.stored_name ){

										var skip_file = $('input[name="'+responseJSON.element+'"]').attr( "skip-uploaded-file-display" );

										var multiple = $('input[name="'+responseJSON.element+'"]').attr( "multiple" );

										//multiple upload
										if( multiple ){
											var i = $('input[name="'+responseJSON.element+'"]').val();
											if( i && i.length > 1 && i != 'none' )i = i + ":::" + responseJSON.stored_name;
											else i = responseJSON.stored_name;
											$('input[name="'+responseJSON.element+'"]').val( i );
										}else{

											//single upload
											$('input[name="'+responseJSON.element+'"]').val( responseJSON.stored_name );

										}

										var dm = responseJSON.element + '_json';
										if( $('textarea[name="'+ dm + '"]') ){

											var jd = {};
											var jdv = $('textarea[name="'+ dm +'"]').val();
											if( jdv && vmultiple ){
												var jd = JSON.parse( jdv );
												if( ! jd ){
													jd = {};
												}
											}
											jd[ md5( responseJSON.stored_name ) ] = responseJSON;

											$('textarea[name="'+ dm + '"]').val( JSON.stringify( jd ) );

											var h = '';

											if( ! $.isEmptyObject( jd ) ){
												$.each(jd, function( jk, jv ){
													h += '<div style="color:#00cc00; margin-bottom:4px;">'+ jv.oname +' <a id="uploaded-'+ jk +'" data-name="'+ responseJSON.element +'" data-stored_name="'+ jv.stored_name +'" href="#" class="pull-right btn btn-sm dark" title="Delete uploaded file" onclick="$.fn.cProcessForm.removeUploadedFile( '+ "'"+ jk +"'" +' );"><i class="icon-trash"></i></a></div>';
												});
											}

											$('#' + responseJSON.element + '-file-content' )
												.html( h );
										}

										if( ! skip_file ){
											switch( responseJSON.ext ){
												case "JPG":
												case "JPEG":
												case "jpg":
												case "jpeg":
												case "png":
												case "gif":
												case "tif":
												case "tiff":
												case "bmp":
													$('img#'+responseJSON.element+'-img')
														.attr( "src", responseJSON.fullname + responseJSON.filename + "." + responseJSON.ext )
														.slideDown(1000 , function(){
															//$('.qq-upload-success').empty();
														});
													break;
											}
										}

										if( $('input[name="'+responseJSON.element+'"]').attr('hide_on_select') ){
											if( $('input[name="'+responseJSON.element+'"]').val() ){
												$('#upload-box-' + responseJSON.element ).find('.qq-upload-button').hide();
											}
										}

										$('#upload-box-' + responseJSON.element ).find('ul.qq-upload-list').empty();
									}
								}else{
									//alert('failed');
								}
								$(".cell-element").find("input[type='file']").addClass("form-control").css("fontSize", "11px");
							}
						});

						//$('input[name="'+ name +'"]').attr( 'id' , id );
						if( $('input[name="'+ name +'"]').attr('hide_on_select') ){
							if( $('input[name="'+ name +'"]').attr('data-value') ){
								$('#upload-box-' + name ).find('.qq-upload-button').hide();
							}
						}

						$(".cell-element").find("input[type='file']").addClass("form-control").css("fontSize", "11px");
					})
					.addClass('upload-activated');

				$('.remove-uploaded-file')
					.not('.activated')
					.on('click', function( e ){
						e.preventDefault();
						if( $(this).attr('alt') ){
							var d = $(this).attr('default-image');
							if( ! d )d = '';
							$( 'input[name="' + $(this).attr('alt') + '"]' ).val( d );
							$(this).parent().empty();

							var dm1 = $(this).attr('alt');
							if( $('input[name="'+dm1+'"]').attr('hide_on_select') ){
								$('#upload-box-' + dm1 ).find('.qq-upload-button').show();
							}
						}

					})
					.addClass("activated");
			}
		},
		removeUploadedFile:function( id ){
			if( id ){
				var $a = $('a#uploaded-' + id);

				if( $a.attr('data-name') ){
					var sn = $a.attr('data-stored_name');

					var dm1 = $a.attr('data-name');
					var dm = dm1 + '_json';

					if( sn && $('input[name="'+dm1+'"]') ){
						var js = $('input[name="'+dm1+'"]').val().split(':::');
						var js2 = [];

						if( js.length > 0 ){
							for( var x = 0; x < js.length; x++ ){
								if( js[ x ] && js[ x ] != sn ){
									js2.push( js[ x ] );
								}
							}
						}

						$('input[name="'+dm1+'"]').val( js2.join(':::') );
						if( $('input[name="'+dm1+'"]').attr('hide_on_select') ){
							if( $('input[name="'+dm1+'"]').val() == '' ){
								$('#upload-box-' + dm1 ).find('.qq-upload-button').show();
							}
						}
					}

					if( $('textarea[name="'+ dm + '"]') ){

						var jd = {};
						var jdv = $('textarea[name="'+ dm +'"]').val();
						if( jdv ){
							var jd = JSON.parse( jdv );
							if( ! jd ){
								jd = {};
							}
						}

						if( jd[ id ] ){
							delete jd[ id ];
						}

						$('textarea[name="'+ dm + '"]').val( JSON.stringify( jd ) );
					}

					$('a#uploaded-' + id).parent().remove();
					$('img#'+dm1+'-img').attr( "src", "" ).hide();
				}
			}
		},
		notificationTimerID:"",
		display_notification:function ( data ){
			if( data && data.typ ){
				var theme = 'alert-danger';

				if( data.theme ){
					theme = data.theme;
				}

				switch(data.typ){
					case "search_cleared":
					case "report_generated":
					case "searched":
					case "saved":
					case "jsuerror":
					case "uerror":
					case "success":
					case "error":
					case "deleted":
					case "serror":
						var html = '<div class="alert ' + theme + ' alert-block1 alert-dismissable">';
						html += '<button type="button" class="close" id="alert-close-button" data-dismiss="alert"></button>';
						if(  data.err )html += '<h4>' + data.err + '</h4>';
						html += data.msg;
						html += '</div>';

						var $notification_container = $('#notification-container');
						if( data.notification_container ){
							$notification_container = $(data.notification_container);
							data.manual_close = 1;
						}else{
							if( $.fn.cProcessForm.notificationTimerID )clearTimeout( $.fn.cProcessForm.notificationTimerID );
						}

						/* @mbay4
                        if( data.html )return html;
                         */

						$notification_container
							.text( html );

						switch(data.typ){
							case "report_generated":
								set_function_click_event();
								break;
							default:
								if( ! data.manual_close ){
									$.fn.cProcessForm.notificationTimerID = setTimeout( function(){
										$('#notification-container')
											.empty();
									} , 6000 );
								}
								break;
						}

						$('#alert-close-button')
							.bind('click', function(){
								$('#notification-container')
									.empty();
							});

						break;
				}
			}

			if( data && data.tok && $('form') ){
				$('form')
					.find('input[name="processing"]')
					.val( data.tok );
			}

			if( data && data.re_process && ! $.fn.cProcessForm.cancelAjaxRecursiveFunction ){
				$.fn.cProcessForm.triggerNewAjaxRequest();
			}
		},
		activateTooltip: function(){

			var $form = $('form.activate-ajax').not('.ajax-activated');

			$form
				.find('.form-gen-element')
				.bind('focus',function(){
					$.fn.cProcessForm.displayTooltip($(this) , $(this).attr('name'), false);
				});

			$form
				.find('.form-gen-element')
				.bind('blur',function(){
					$.fn.cProcessForm.displayTooltip( $(this) , '', true );
				});

			$form
				.find('.form-element-required-field')
				.bind('blur',function(){
					$.fn.cProcessForm.validate( $(this) );
				}).blur();

		},
		activateFormValidation: function( $form ){
			if( ! $form.hasClass('skip-validation') ){
				$.fn.cProcessForm.validate_and_submit_form( $form );
			}
		},
		validate: function( me ){

			if( $.fn.cProcessForm.testdata( me.val() , me.attr('alt') ) ){
				if( me.hasClass('invalid-data') ){
					me.removeClass('invalid-data').addClass('valid-data');
				}else{
					me.addClass('valid-data');
				}
			}else{
				if(me.hasClass('valid-data')){
					me.addClass('invalid-data').removeClass('valid-data');
				}else{
					me.addClass('invalid-data');
				}
			}

		},
		validate_and_submit_form: function( $me ){

			$me
				.find('.form-element-required-field')
				.blur();

			if( $me.find('.form-element-required-field').hasClass('invalid-data') ){
				$me
					.find('.invalid-data:first')
					.focus();

				var html = "<br /><br /><strong>Form Fields with Invalid Data</strong><br />";
				var no = 0;
				$me
					.find('.invalid-data')
					.each(function(){
						++no;
						var ptext = $(this).parents(".input-group").find(".input-group-addon").text();
						if( ! ptext )ptext = $(this).parents(".control-group").find(".control-label").text();
						html += no + ". " + ptext + "<br />";
					});

				$me.data('do-not-submit', '');

				//display notification to fill all required fields
				var data = {
					typ:'jsuerror',
					err:'Invalid Form Field',
					msg:'Please do ensure to correctly fill all required fields with appropriate values' + html,
				};
				$.fn.cProcessForm.display_notification( data );

				return false;
			}

			$me.data('do-not-submit', 'submit');
		},
		pass:0,
		testdata: function (data,id){

			switch (id){
				case 'url':
				case 'text':
				case 'textarea':
				case 'upload':
					if(data.length>0)return 1;
					else return 0;
					break;
				case 'category':
					if(data.length>11)return 1;
					else return 0;
					break;
				case 'number':
				case 'currency':
					/*/[^0-9\-\.]/g*/
					data = ( data.replace( ",", '' ) );
					return (data - 0) == data && data.length > 0;
					break;
				case 'email':
					var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
					if( data.length < 1 || !emailReg.test( data ) ) {
						return 0;
					} else {
						return 1;
					}
					break;
				case 'password':
					if( $('input[type="password"]:first').val() != $.fn.cProcessForm.pass ){
						$.fn.cProcessForm.pass = 0;
					}

					if( ! $.fn.cProcessForm.pass ){
						//VERIFY PASSWORD
						if( data.length > 6 ){
							/*
							//TEST FOR AT LEAST ONE NUMBER
							var passReg = /[0-9]/;
							if( passReg.test( data ) ) {
								//TEST FOR AT LEAST ONE UPPERCASE ALPHABET
								passReg = /[A-Z]/;
								if( passReg.test( data ) ){
									//TEST FOR AT LEAST ONE LOWERCASE ALPHABET
									passReg = /[a-z]/;
									if( passReg.test( data ) ){
										//STORE FIRST PASSWORD
										pass = data;
										return 1;
									}else{
										//NO LOWERCASE ALPHABET IN PASSWORD
										pass = 0;
										return 0;
									}
								}else{
									//NO UPPERCASE ALPHABET IN PASSWORD
									pass = 0;
									return 0;
								}
							}else{
								//NO NUMBER IN PASSWORD
								pass = 0;
								return 0;
							}
							*/
							$.fn.cProcessForm.pass = data;
							return 1;
						}else{
							$.fn.cProcessForm.pass = 0;
							return 0;
						}
						/*
						a.	User ID and password cannot match
						b.	Minimum of 1 upper case alphabetic character required
						c.	Minimum of 1 lower case alphabetic character required
						d.	Minimum of 1 numeric character required
						e.	Minimum of 8 characters will constitute the password
						*/
					}else{
						//CONFIRM SECOND PASSWORD
						if( data == $.fn.cProcessForm.pass )return 1;
						else return 0;
					}
					break;
					if(data.length>6)return 1;
					else return 0;
					break;
				case 'phone':
				case 'tel':
					var phoneReg = /[a-zA-Z]/;
					if( data.length<5 || phoneReg.test( data ) ) {
						return 0;
					} else {
						return 1;
					}
					break;
				case 'select':
				case 'multi-select':
					return data;
					break;
				case 'datetime-local':
				case 'date':
					return data;
					break;
				default:
					return 0;
			}
		},
		g_report_title: '',
		g_all_signatories_html: '',
		bind_quick_print_function: function(){

			$('body')
				.on('click', 'button#summary-view', function(e){
					$('#example')
						.find('tbody')
						.find('tr')
						.not('.total-row')
						.toggle();
				});

			$('body')
				.on('click', 'a.quick-print', function(e){
					e.preventDefault();

					var html = $.fn.cProcessForm.get_printable_contents( $(this) );

					if( ! g_report_title ){
						$.fn.cProcessForm.g_report_title = $('title').text();
					}

					var x=window.open();
					x.document.open();
					x.document.write( '<link href="'+ $('#print-css').attr('href') +'" rel="stylesheet" />' + '<body style="padding:0;">' + $.fn.cProcessForm.g_report_title + html + $.fn.cProcessForm.g_all_signatories_html + '<script type="text/javascript">setTimeout(function(){ window.print(); } , 500 );</script></body>' );
					x.document.close();
					//x.print();
				});

			$('body')
				.on('click', 'a.print-report-popup', function(e){
					if( $( "#e-report-title" ) && $( "#e-report-title" ).is(":visible") ){
						$('.popover-content')
							.find('form.report-settings-form')
							.find('input[name="report_title"]').val( $( "#e-report-title" ).text() );
					}
				});


			$('body')
				.on('click', 'input.advance-print-preview, input.advance-print, button.quick-print-record', function(e){
					e.preventDefault();
					var html = $.fn.cProcessForm.get_printable_contents( $(this) );

					var report_title = ''; //$('title').html();
					var report_title2 = ''; //$('title').html();

					//var $form = $('.popover-content').find('form.report-settings-form');
					var $form = $(this).parents('form');
					var default2 = 1;

					if( $(this).attr('data-share') || $(this).attr('data-emails') ){
						$form = $(this).parents('span.report-buttons-con').find('form[name="report_settings_form"]');
						default2 = 0;
					}


					var purl = $form.find('input[name="print_url"]').val();
					var r_title = $form.find('input[name="report_title"]').val();
					var r_sub_title = $form.find('input[name="report_sub_title"]').val();

					var orientation = $form.find('select[name="orientation"]').val();
					var paper = $form.find('select[name="paper"]').val();

					var rfrom = $form.find('input[name="report_from"]').val();
					var rto = $form.find('input[name="report_to"]').val();
					var rref = $form.find('input[name="report_ref"]').val();

					var r_type = '';
					var r_type = 'mypdf';
					var r_user_info = '';
					var exclude_header = 0;

					if( $(this).hasClass( 'advance-print' ) ){
						var r_type = $form.find('input[name="report_type"]').filter(':checked').val();

						if( $form.find('input[name="report_show_user_info"]').is(':checked') ){
							var r_user_info = 'yes';
						}
					}

					if( $form.find('input[name="exclude_header"]').is(':checked') ){
						exclude_header = 1;
					}

					var r_signatory = $form.find('input[name="report_signatories"]').val();

					var r_template = $form.find('select[name="report_template"]').val();
					var r_ainfo = $form.find('input[name="additional_info"]').val();

					$.fn.cProcessForm.g_all_signatories_html = '';
					$.fn.cProcessForm.g_report_title = '';

					if( r_title && default2 ){
						report_title = '<h3 style="text-align:center;">' + r_title + ' ';

						if( r_sub_title ){
							report_title += '<small style="display:block;">' + r_sub_title + '</small>';
						}

						report_title += '</h3>';

						$.fn.cProcessForm.g_report_title = report_title;
					}

					var direct_print = 0;
					if( $(this).hasClass("direct-print") )
						direct_print = 1;

					var all_signatories_html = '';
					var signatories_html = '';
					if( r_signatory ){
						if( $form.find('#report-signatory-fields').is(':visible') ){

							signatories_html = '<table width="100%">';

							$form
								.find('.signatory-fields')
								.each( function(){
									if( $(this).val() ){
										signatories_html += '<tr><td width="20%">' + $(this).val() + '</td><td style="border-bottom:1px solid #dddddd;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>';
									}
								} );

							signatories_html += '</table>';
						}

						all_signatories_html = '<div><table width="100%"><tr>';

						for( var i = 0; i < r_signatory; i++ ){
							all_signatories_html += '<td style="padding:10px;">';
							all_signatories_html += signatories_html;
							all_signatories_html += '</td>';
						}

						all_signatories_html += '</tr></table></div>';

						$.fn.cProcessForm.g_all_signatories_html = all_signatories_html;
					}

					switch( r_type ){
						case "mypdf":
							var dx = {html:report_title + html + all_signatories_html, current_module:$('#active-function-name').attr('function-class'), current_function:$('#active-function-name').attr('function-id'), report_title:report_title2, report_show_user_info:r_user_info , orientation:orientation, paper:paper, rfrom:rfrom, rto:rto, rref:rref, report_template:r_template, info:r_ainfo, direct_print:direct_print, exclude_header:exclude_header };

							if( purl )dx.print_url = purl;

							var sprompt = '';
							var emails = '';
							if( $(this).attr('data-prompt') ){
								sprompt = $(this).attr('data-prompt');
							}
							if( $(this).attr('data-emails') ){
								emails = $(this).attr('data-emails');
								dx.send_email = 1;
							}

							if( $(this).attr('data-share') ){
								dx.share_type = $(this).attr('data-share');
								//dx.report_title = $(this).attr('data-title');
							}

							if( sprompt ){
								emails = prompt( sprompt, emails );
								dx.send_email = 1;
							}

							if( dx.send_email ){
								if( ! emails ){
									var data = {theme:'alert-info', err:'No Recipient Email Address', msg:'Please specify a recipient email address', typ:'jsuerror' };
									$.fn.cProcessForm.display_notification( data );
									return false;
								}
								dx.emails = emails;
								if( $(this).attr("data-email-subject") ){
									dx.email_subject = $(this).attr("data-email-subject");
								}
							}

							var hidden_fields = {};
							$form
								.find("input.hidden-fields")
								.each(function(){
									hidden_fields[ $(this).attr('name') ] = $(this).val();
								});

							dx["hidden_fields"] = JSON.stringify( hidden_fields );


							$.fn.cProcessForm.ajax_data = {
								ajax_data: dx,
								form_method: 'post',
								ajax_data_type: 'json',
								ajax_action: 'request_function_output',
								ajax_container: '',
								//ajax_get_url: '?action='+r_type+'&todo=generate_pdf',
								ajax_get_url: '?action='+r_type+'&todo=generate_html_front',
							};
							$.fn.cProcessForm.ajax_send();
							break;
						case "csv":
						case "myexcel":
							var todo = 'generate_excel_front';

							switch( r_type ){
								case "csv":
									r_type = 'myexcel';
									html = '';
									todo = 'generate_csv';
									break;
							}

							var dx = {html:html, current_module:$('#active-function-name').attr('function-class'), current_function:$('#active-function-name').attr('function-id') , report_title:report_title, rfrom:rfrom, rto:rto, rref:rref, report_template:r_template, info:r_ainfo };

							if( purl )dx.print_url = purl;

							var hidden_fields = {};
							$form
								.find("input.hidden-fields")
								.each(function(){
									hidden_fields[ $(this).attr('name') ] = $(this).val();
								});

							dx["hidden_fields"] = JSON.stringify( hidden_fields );

							$.fn.cProcessForm.ajax_data = {
								ajax_data: dx,
								form_method: 'post',
								ajax_data_type: 'json',
								ajax_action: 'request_function_output',
								ajax_container: '',
								ajax_get_url: '?action='+r_type+'&todo=' + todo,
							};
							$.fn.cProcessForm.ajax_send();
							break;
						default:
							var x=window.open();
							x.document.open();
							var h = '';
							if( $(this).hasClass( 'advance-print' ) ){
								h = '<script type="text/javascript">setTimeout( function(){ window.print(); }, 500 );</script>';
							}
							x.document.write( '<link href="'+ $('#print-css').attr('href') +'" rel="stylesheet" />' + '<body style="padding:0;"><div id="watermark"></div>' + report_title + html + all_signatories_html + h + '</body>' );
							x.document.close();

							break;
					}
				});

		},
		get_printable_contents: function( $printbutton ){
			var html = '';

			if( $printbutton.attr('merge-and-clean-data') && $printbutton.attr('merge-and-clean-data') == 'true' ){
				var $content = $( $printbutton.attr('target') ).clone();

				//Get Records
				var target_table = $printbutton.attr('target-table');
				var tbody = $content.find(target_table).find('tbody');

				//Remove Action Button Column
				tbody.find('.view-port-hidden-table-row').remove();
				tbody.find('.remove-before-export').parents('td').remove();

				tbody.find('.hide-custom-view-select-classes').remove();

				tbody.find('.line-items-space-row').find("td").html("");

				//Get Heading
				var thead = $content.find('.dataTables_scrollHeadInner').find('thead');
				if( thead ){
					thead.find('th').css('width','auto');

					//Remove Action Button Column
					thead.find('.remove-before-export').parents('th').remove();
					thead.find('.remove-before-export').remove();

					//Adjust Colspan
					thead
						.find('.change-column-span-before-export')
						.attr('colspan', thead.find('.change-column-span-before-export').attr('exportspan') );
				}

				//Get Screen Data
				html = '<div id="dynamic"><table class="'+$content.find(target_table).attr('class')+'" width="100%" style="position:relative;" cellspacing="0" cellpadding="0"><thead>'+thead.html()+'</thead><tbody>'+tbody.html()+'</tbody></table></div>';
			}else{
				html = $( $printbutton.attr('target') ).html();
			}

			return html;
		},
		displayTooltip: function( me, name, removetip ){

			if( removetip ){
				$('#ogbuitepu-tip-con').fadeOut(800);
				return;
			}

			//Check if tooltip is set
			if( me && me.attr('tip') && me.attr('tip').length > 1){
				$('#ogbuitepu-tip-con')
					.find('> div')
					.html(me.attr('tip'));

				//Display tooltip
				//var offsetY = 8;
				var offsetY = 0;
				var offsetX = 12;

				//var left = me.offset().left - (offsetX + $('#ogbuitepu-tip-con').width() );
				//var top = (me.offset().top + ((me.height() + offsetY)/2)) - ($('#ogbuitepu-tip-con').height()/2);

				var left = me.offset().left;
				//var top = (me.offset().top + ((me.height() + offsetY)));
				var top = (me.offset().top + ((me.outerHeight(true) + offsetY)));

				if( parseFloat( name ) ){
					top = (name) - ($('#ogbuitepu-tip-con').height()/2);
				}

				$('#ogbuitepu-tip-con')
					.find('> div')
					.css({
						padding:me.css('padding'),
					});

				$('#ogbuitepu-tip-con')
					.css({
						width:me.width()+'px',
						top:top,
						left:left,
					})
					.fadeIn(800);
			}else{
				//Hide tooltip container
				$('#ogbuitepu-tip-con').fadeOut(800);
			}
		},
		requestRetryCount: 0,
		progress_bar_timer_id: 0,
		progress_bar_change: function(){
			var total = 120;
			var step = 1;

			if( $.fn.cProcessForm.progress_bar_timer_id )
				clearTimeout( $.fn.cProcessForm.progress_bar_timer_id );

			if( $.fn.cProcessForm.function_click_process == 0 ){
				var $progress = $('.virtual-progress-bar:visible').find('.progress-bar');

				if($progress.data('step') && $progress.data('step')!='undefined'){
					step = $progress.data('step');
				}

				var percentage_step = ( step / total ) * 100;
				++step;

				if( percentage_step > 100 ){
					$progress
						.css('width', '100%');

					$('.virtual-progress-bar')
						.remove();

					$('.progress-bar-container')
						.html('');

					//Refresh Page
					$.fn.cProcessForm.function_click_process = 1;

					++$.fn.cProcessForm.requestRetryCount;

					//Stop All Processing
					window.stop();

					//check retry count
					if( $.fn.cProcessForm.requestRetryCount > 1 ){
						//display no network access msg
						//requestRetryCount = 0;

						var settings = {
							message_title:'No Network Access',
							message_message: 'The request was taking too long!',
							auto_close: 'no'
						};
						display_popup_notice( settings );

						internetConnection = false;
					}else{
						//display retrying msg

						var settings = {
							message_title:'Refreshing...',
							message_message: 'Please Wait.',
							auto_close: 'yes'
						};
						//$.fn.cProcessForm.display_popup_notice.call( settings );

						//request resources again
						$.fn.cProcessForm.ajax_send.call();

					}

				}else{
					$progress
						.data('step',step)
						.css('width', percentage_step+'%');

					$.fn.cProcessForm.progress_bar_timer_id = setTimeout(function(){
						$.fn.cProcessForm.progress_bar_change.call();
					},1000);
				}
			}else{
				$('.virtual-progress-bar')
					.find('.progress-bar')
					.css('width', '100%');

				setTimeout(function(){
					$('.virtual-progress-bar')
						.remove();

					$('.progress-bar-container')
						.html('');
				},800);
			}
		},
		openImageCapture: function(){
			$("#capture-image-button").hide();
			$("#close-image-capture")
				.text( "Close" )
				.attr( "disabled", false );
		},
		closeImageCapture: function( action ){

			$("#close-image-capture")
				.text( "Processing..." )
				.attr( "disabled", true );

			var img = $("#capture-container").find("iframe").contents().find('input[name="image"]').val();

			if( img ){

				$.fn.cProcessForm.ajax_data = {
					ajax_data: {image: img },
					form_method: 'post',
					ajax_data_type: 'json',
					ajax_action: 'request_function_output',
					ajax_container: '',
					ajax_get_url: action,
				};
				$.fn.cProcessForm.ajax_send();


			}else{
				var data = {theme:'alert-info', err:'No Captured Image', msg:'No image was captured, to capture an image click on the SNAP PHOTO button before you close the capture screen', typ:'jsuerror' };
				$.fn.cProcessForm.display_notification( data );
				$.fn.cProcessForm.saveCapturedImage();
			}
		},
		saveCapturedImage: function(){
			if( $.fn.cProcessForm.returned_ajax_data && $.fn.cProcessForm.returned_ajax_data.stored_path && $.fn.cProcessForm.returned_ajax_data.full_path ){
				var element = "image";
				$('input[name="'+ element +'"]').val( $.fn.cProcessForm.returned_ajax_data.stored_path );
				$('img#'+ element +'-img')
					.attr( "src", $.fn.cProcessForm.returned_ajax_data.full_path )
					.slideDown(1000 , function(){
						$('.qq-upload-success').empty();
					});
			}
			$("#capture-container").html("");
			$("#capture-image-button").show();
		},
		getSignature: function( captureType, container ) {

			if(  captureType && $("input#id-"+captureType+"-sign") ){
				if(  container && $("#"+container) ){
					var img = $("#"+container).find("iframe").contents().find('input[name="image"]').val();
					var h = '';

					if( img ){
						/*
						$.fn.cProcessForm.ajax_data = {
							ajax_data: {image: img },
							form_method: 'post',
							ajax_data_type: 'json',
							ajax_action: 'request_function_output',
							ajax_container: '',
							ajax_get_url: action,
						};
						$.fn.cProcessForm.ajax_send();
						*/
						$("input#id-"+captureType+"-sign").val( img );
						h = '<img src="'+ img +'" style="max-width:100%;" />';
					}else{
						var data = {theme:'alert-info', err:'No Signature Captured', msg:'No signature was captured, to capture a signature click on the SAVE SIGNATURE button before you close the capture screen', typ:'jsuerror' };
						$.fn.cProcessForm.display_notification( data );
					}

					$("#"+container).html( h );
				}else{
					var data = {theme:'alert-danger', err:'No Signature Pad Form', msg:'Signature cannot be captured', typ:'jsuerror' };
					$.fn.cProcessForm.display_notification( data );
				}
			}else{
				var data = {theme:'alert-danger', err:'No Form Found', msg:'Signature cannot be captured', typ:'jsuerror' };
				$.fn.cProcessForm.display_notification( data );
			}
		},
		activatedEmptyTab:0,
		activateEmptyTab: function(){
			$("a.empty-tab")
				.on("click", function(){
					$("#dash-board-main-content-area").html( $("#loading-tab").html() );
				});

			$("a.more-tab")
				.on("click", function(){

					if( $('.more-tab-added').is(":visible") ){

						$('.more-tab-added')
							.prependTo( $("#more-tab-handle").find("ul") )
							.removeClass('more-tab-added')
							.find('a')
							.removeClass('active')
							.removeClass('nav-link');
					}

					$(this)
						.addClass('nav-link')
						.parent()
						.insertBefore("#more-tab-handle")
						.addClass('more-tab-added');

				});

			$.fn.cProcessForm.activatedEmptyTab = 1;
		},
		makePayment: function(){
			var edata = {};

			if( $.fn.cProcessForm.returned_ajax_data && $.fn.cProcessForm.returned_ajax_data.data && $.fn.cProcessForm.returned_ajax_data.data.payment_method ){
				var data = $.fn.cProcessForm.returned_ajax_data.data;
				var gid = data.id;
				var hr = '';
				if( data.html_replacement_selector ){
					hr = data.html_replacement_selector;
				}

				switch( data.payment_method ){
					case "rave":
						FlutterwaveCheckout({
							public_key: "FLWPUBK_TEST-61d1ddcecc9720bafe4b857df714d14a-X",
							tx_ref: "hyella-coop-" + (Math.random().toString(36).substring(7) || Math.random().toString(36).substr(2, 5)) + '-' + data.id,
							amount: Math.floor(data.amount_paid),
							currency: data.currency.toUpperCase(), //"NGN",
							payment_options: data.payment_options, //"card,mobilemoney,ussd",
							customer: {
								email: data.payer.email,
								phonenumber: data.payer.phone,
								name: data.payer.name,
							},
							onclose: function() {

								// close modal
								$('#' + hr ).html( 'Payment Cancelled' );

								d = { id : gid }
								$.fn.cProcessForm.ajax_data = {
									ajax_data: d,
									form_method: 'post',
									ajax_data_type: 'json',
									ajax_action: 'request_function_output',
									ajax_container: '',
									ajax_get_url: "?action=online_payment&todo=cancel_payment&html_replacement_selector=" + hr,
								};
								$.fn.cProcessForm.ajax_send();

								//console.log('clo' + gid); alert(12);
							},
							callback: function (response) {
								//console.log(response);// alert(2); return false;
								switch ( response.status ){
									case "successful":
									case "completed":
										d = { id : gid, status: 'completed'}
										$.fn.cProcessForm.ajax_data = {
											ajax_data: d,
											form_method: 'post',
											ajax_data_type: 'json',
											ajax_action: 'request_function_output',
											ajax_container: '',
											ajax_get_url: "?action=online_payment&todo=update_payment_status",
										};
										$.fn.cProcessForm.ajax_send();
										break;
									case 'error':
										var edata = {theme:'alert-danger note note-danger', err:'Rave Payment Failed', msg:'Please try again', typ:'uerror' };
										return $.fn.cProcessForm.display_notification( edata );
										break;
								}
							},
							customizations: {
								title: data.payment_category,
								description: "Hyella Cooperarive Module Payment",
								logo: "http://localhost:819/coop/www/icon.png",
							},
						});
						break;
					case "pay_stack": //use this
						let handler = PaystackPop.setup({
							key: 'pk_test_6ac473dd058d051a234dbfdc1025f3204a0384df', // Public key
							email: data.payer.email,
							amount:  Math.floor(data.amount_paid) * 100, // You have to multiply amount by 100 for it to work currectly
							firstname:  data.payer.first_name,
							lastname: data.payer.name,
							onClose: function(){
								//alert('Transaction Not completed. Window closed.');
								$('#' + hr ).html( 'Payment Cancelled' );

								d = { id : gid }
								$.fn.cProcessForm.ajax_data = {
									ajax_data: d,
									form_method: 'post',
									ajax_data_type: 'json',
									ajax_action: 'request_function_output',
									ajax_container: '',
									ajax_get_url: "?action=online_payment&todo=cancel_payment&html_replacement_selector=" + hr,
								};
								$.fn.cProcessForm.ajax_send();
							},
							callback: function(response){
								console.log( response );
								switch(response.status){
									case "success":
										d = { id : gid, status: 'completed' }
										$.fn.cProcessForm.ajax_data = {
											ajax_data: d,
											form_method: 'post',
											ajax_data_type: 'json',
											ajax_action: 'request_function_output',
											ajax_container: '',
											ajax_get_url: "?action=online_payment&todo=update_payment_status&html_replacement_selector=" + hr,
										};
										$.fn.cProcessForm.ajax_send();
										break;
									default:
										d = response;
										d.id = gid; d.status = 'failed';
										$.fn.cProcessForm.ajax_data = {
											ajax_data: d,
											form_method: 'post',
											ajax_data_type: 'json',
											ajax_action: 'request_function_output',
											ajax_container: '',
											ajax_get_url: "?action=online_payment&todo=update_payment_status&html_replacement_selector=" + hr,
										};
										$.fn.cProcessForm.ajax_send();
										break;
								}
							},
						});
						handler.openIframe();
						break;
					default:
						edata = {theme:'alert-danger note note-danger', err:'Invalid Payment Method', msg:'Please select a valid payment method and try again', typ:'uerror' };
						break;
				}

			}else{
				edata = {theme:'alert-danger note note-danger', err:'Unspecified Payment Method', msg:'Please select a payment method and try again', typ:'uerror' };
			}

			if( edata && edata.msg ){
				return $.fn.cProcessForm.display_notification( edata );
			}
		},
		addComma: function( nStr ){
			nStr += '';
			x = nStr.split('.');
			x1 = x[0];
			x2 = x.length > 1 ? '.' + x[1] : '';
			var rgx = /(\d+)(\d{3})/;
			while (rgx.test(x1)) {
				x1 = x1.replace(rgx, '$1' + ',' + '$2');
			}
			return x1 + x2;
		},
		selectElementID: '',
		selectElementContents: function ( id ){
			var data = {
				typ:'saved',
				err:'Copying in Progress',
				msg:'Please wait...',
				theme:' warning note-warning ',
			};
			$.fn.cProcessForm.display_notification( data );
			$.fn.cProcessForm.selectElementID = id;

			setTimeout(function(){
				var el = document.getElementById( $.fn.cProcessForm.selectElementID );

				var body = document.body, range, sel;
				if (document.createRange && window.getSelection) {
					range = document.createRange();
					sel = window.getSelection();
					sel.removeAllRanges();
					try {
						range.selectNodeContents(el);
						sel.addRange(range);
					} catch (e) {
						range.selectNode(el);
						sel.addRange(range);
					}
					document.execCommand("copy");

				} else if (body.createTextRange) {
					range = body.createTextRange();
					range.moveToElementText(el);
					range.select();
					range.execCommand("Copy");
				}

				var data = {
					typ:'saved',
					manual_close:1,
					err:'Successfully Copied',
					msg:'Report has been successfully copied<br /><br />Open your excel or word document and paste the contents there',
					theme:' note note-success ',
				};
				$.fn.cProcessForm.display_notification( data );
			}, 1000 );
			//alert("Report has been successfully copied\n\nOpen your excel or word document and paste the contents there");
		},
		localStore: function( key, value, options, type ){

			switch( type ){
				case "delete":
					amplify.store( key, null );
					break;
				case "delete_all":
					$.each( amplify.store(), function (storeKey) {
						// Delete the current key from Amplify storage
						amplify.store(storeKey, null);
					});
					break;
				case "get":
					return amplify.store( key );
					break;
				case "put":
					amplify.store( key, value, options );
					break;
			}

		},
	}
}(jQuery));

function display_popup_notice( settings ){
	var theme = 'a';
	var html = settings.message_title + "\n" + settings.message_message;
	alert( html );

	/*
    $('.pass-code-auth').slideDown();
    $('.processing-pass-code-auth').hide();
    $('.successful-pass-code-auth').hide();
    */
};

var gCheck_sum = '';


function set_function_click_event(){
};

function prepare_new_record_form_new(){
	$.fn.cProcessForm.activateAjaxForm();
};

$.fn.cProcessForm.activateAjaxRequestButton();

function activate_highcharts(){ $.fn.cProcessForm.activate_highcharts(); };
function activate_and_export_highcharts(){ $.fn.cProcessForm.activate_and_export_highcharts(); };



var nwResizeWindow = function () {

	return {
		//main function to initiate the module
		e1: "#dash-board-main-content-area",
		e2: ".dynamic", //dataTables_scrollBody
		e3: "#excel-import-form-container",
		e4: "#chart-container-parent",
		e5: ".resizable-height",
		init: function () {
			$(window).on("resize", function(){
				nwResizeWindow.resizeWindow();
			});
		},
		resizeWindow: function () {

			var sel = nwResizeWindow.e2;
			if( $(sel).is(":visible") ){
				if( $(sel).length > 1 ){
					$(sel).each(function(){
						nwResizeWindow.resizeWindowAction( $(this) );
					});
				}else{
					nwResizeWindow.resizeWindowAction(sel);
				}
			}else{
				var sel = nwResizeWindow.e3;
				if( $(sel).is(":visible") ){
					nwResizeWindow.resizeWindowAction(sel);
				}else{
					var sel = nwResizeWindow.e4;
					if( $(sel).is(":visible") ){
						nwResizeWindow.resizeWindowAction(sel);
					}
				}
			}

			var sel = nwResizeWindow.e5;
			if( $(sel).is(":visible") ){
				nwResizeWindow.resizeWindowAction(sel);
			}

			$(document).scrollTop(0);
		},
		resizeWindowAction: function ( sel ) {

			if( sel && $( sel ) && $( sel ).offset() ){
				var top = $( sel ).offset().top;
				var docHeight = window.innerHeight; //$(window).height();

				var h = docHeight - top;
				if( h > 55 ){
					var oh = h;

					$( sel ).each(function(){
						h = oh;

						//if( $( this ).hasClass("dataTables_scrollBody") ){ h -= 55; }
						if( $( this ).hasClass("dynamic") ){
							h -= 42; //52
						}

						if( $( this ).attr("data-subtract") ){
							var hs = parseFloat( $( this ).attr("data-subtract") * 1 );
							if( isNaN(hs) )hs = 0;

							h -= hs;
						}

						$( this )
							.css("height", h );

						if( $( this ).hasClass("auto-scroll") ){
							$( this )
								.css("max-height", h )
								.css("overflow-y", "scroll" );
						}

					});
				}else{
					//alert("Screen Height Too Small \n\nPlease Maximize you window");
				}
			}

		},
		resizeWindowImport: function () {

			var sel = nwResizeWindow.e3;
			nwResizeWindow.resizeWindowAction( sel );

		},
		resizeWindowChart: function () {

			var sel = nwResizeWindow.e4;
			nwResizeWindow.resizeWindowAction( sel );

		},
		resizeWindowHeight: function () {

			var sel = nwResizeWindow.e5;
			nwResizeWindow.resizeWindowAction( sel );

		},
		adjustColumnSizing: function () {
			setTimeout( function(){

				$( "table.activated-table")
					.dataTable()
					.fnAdjustColumnSizing();
			}, 1000 );
		},
		adjustBarChart: function () {
			$( "#chart-container")
				.parent()
				.remove();

			$( "#chart-container-1")
				.parent()
				.removeClass("col-md-7")
				.addClass("col-md-10");

		},
	};

}();

var nwDisplayNotification = function () {
	return {
		notificationTimerID: "",
		display_notification: function ( data ){
			$.fn.cProcessForm.display_notification( data );
		}

	};
}();


var nwTreeView = function () {
	return {
		selector: "#ui-navigation-tree",
		todo: "",
		action: "",
		callback: "",
		data: {},
		refresh_tree_view: function(){
			if( $( nwTreeView.selector ) ){
				var instance = $( nwTreeView.selector ).jstree(true);
				if( instance )instance.refresh();
			}
		},
		setParamsAndDisplayTree: function(){

			if( $.fn.cProcessForm.returned_ajax_data && $.fn.cProcessForm.returned_ajax_data.tree_view_data && $.fn.cProcessForm.returned_ajax_data.tree_view_data.action && $.fn.cProcessForm.returned_ajax_data.tree_view_data.todo ){

				nwTreeView.action = $.fn.cProcessForm.returned_ajax_data.tree_view_data.action;
				nwTreeView.todo = $.fn.cProcessForm.returned_ajax_data.tree_view_data.todo;

				nwTreeView.data = {};
				if( $.fn.cProcessForm.returned_ajax_data.tree_view_data.data ){
					nwTreeView.data = $.fn.cProcessForm.returned_ajax_data.tree_view_data.data;
				}

				if( $.fn.cProcessForm.returned_ajax_data.tree_view_data.selector ){
					nwTreeView.selector = $.fn.cProcessForm.returned_ajax_data.tree_view_data.selector;
				}

				nwTreeView.activate_tree_view_main();
			}

		},
		activate_tree_view_main: function(){

			var action = nwTreeView.action;
			var record_id = "";
			var todo = nwTreeView.todo;
			var operator_id = "";

			var params = '&' + jQuery.param( nwTreeView.data );

			//var surl = $.fn.cProcessForm.requestURL + 'php/ajax_request_processing_script.php?action='+action+'&todo='+todo + params;
			var surl = $.fn.cProcessForm.requestURL + 'Endpoint?action=treeview&nwp2_action='+action+'&todo='+todo + params;


			$( nwTreeView.selector )
				//.bind("refresh.jstree", function () {
				.bind("loaded.jstree", function () {
					if( nwTreeView.data && nwTreeView.data.click_first_item ){
						$( nwTreeView.selector )
							.find('ul > li > a:first')
							.click();
					}
				})
				.on("changed.jstree", function (e, data) {
					if(data.selected.length) {
						switch( $(this).attr("id") ){
							case 'reports-table-of-content-tree-view':
								var d = data.instance.get_node(data.selected[0]).id;
								$('iframe#iframe-container-of-content')
									.attr( 'src', $('iframe#iframe-container-of-content').attr('data-src') + '#' + d );

								$(document).scrollTop(0);
								break;
							default:
							case 'ui-navigation-tree':
							case 'move-ui-navigation-tree':
								//console.log(data.instance.get_node(data.selected[0]));
								var d = data.instance.get_node(data.selected[0]).id;
								var ids = d.split(':::');
								var act = "";

								if( ids.length > 1 ){
									var ajax_data = {};

									for( var x = 0; x < ids.length; x++ ){
										if( ids[ x ] ){
											var d2 = ids[ x ].split('=');
											if( d2.length > 1 ){
												ajax_data[ d2[0] ] = d2[1];
												if( act ){
													act += "&";
												}else{
													act = "?";
												}
												act += d2[0] + "=" + d2[1];
											}
										}
									}

									switch( $(this).attr("id") ){
										case 'move-ui-navigation-tree':

											if( ajax_data["id"] && $('#selected-destination-folder-id') ){
												$('#selected-destination-folder-id').val( ajax_data["id"] );
												$('#selected-destination-folder').html( data.instance.get_node(data.selected[0]).text );
											}
											break;
										default:
										case 'ui-navigation-tree':

											$.fn.cProcessForm.ajax_data = {
												ajax_data: ajax_data,
												form_method: 'post',
												ajax_data_type: 'json',
												ajax_action: 'request_function_output',
												ajax_container: '',
												ajax_get_url: act,
												//ajax_get_url: "",
											};
											$.fn.cProcessForm.ajax_send();
											break;
									}

								}
								break;
						}
					}
				})
				.jstree({
					'core' : {
						'data' : {
							"type": "POST",
							"url" : surl,
							"dataType" : "json", // needed only if you do not supply JSON headers
							"data" : function (node) {
								return { "id" : node.id };
							}
						}
					}
				});

		},

	};
}();


/*
if (!window.DOMTokenList) {
  Element.prototype.containsClass = function(name) {
    return new RegExp("(?:^|\\s+)" + name + "(?:\\s+|$)").test(this.className);
  };

  Element.prototype.addClass = function(name) {
    if (!this.containsClass(name)) {
      var c = this.className;
      this.className = c ? [c, name].join(' ') : name;
    }
  };

  Element.prototype.removeClass = function(name) {
    if (this.containsClass(name)) {
      var c = this.className;
      this.className = c.replace(
          new RegExp("(?:^|\\s+)" + name + "(?:\\s+|$)", "g"), "");
    }
  };
}

// sse.php sends messages with text/event-stream mimetype.
var source = new EventSource('../engine/php/sse.php');

function closeConnection() {
  source.close();
  updateConnectionStatus('Disconnected', false);
}

function updateConnectionStatus(msg, connected) {
  var el = document.querySelector('#connection');
  if (connected) {
    if (el.classList) {
      el.classList.add('connected');
      el.classList.remove('disconnected');
    } else {
      el.addClass('connected');
      el.removeClass('disconnected');
    }
  } else {
    if (el.classList) {
      el.classList.remove('connected');
      el.classList.add('disconnected');
    } else {
      el.removeClass('connected');
      el.addClass('disconnected');
    }
  }
  el.innerHTML = msg + '<div></div>';
}

source.addEventListener('message', function(event) {
  if( event.data ){
  var data = JSON.parse(event.data);

  var options = {
        iconUrl: data.pic,
        title: data.title,
        body: data.msg+"\n"+data.host,
        timeout: 5000, // close notification in 1 sec
        onclick: function () {
            //console.log('Pewpew');
        }
    };
	if ( $("#push-notification-support") ) {
		var notification = $.notification(options)
		.then(function (notification) {
			//window.focus();
			//console.log('Ok!');
		}, function (error) {
			console.error('Rejected with status ' + error);
		});
		console.log('receive', data.check_sum );
		console.log('receiveG', gCheck_sum );

		if( data.check_sum && gCheck_sum != data.check_sum )
			authenticated_visitor( {visitor_data: data, url:$.fn.cProcessForm.requestURL } );

		$('.b-level').text('Notifications are ' + $.notification.permissionLevel());
	}
  }
}, false);

source.addEventListener('open', function(event) {
  updateConnectionStatus('Connected', true);
}, false);

source.addEventListener('error', function(event) {
  if (event.eventPhase == 2) { //EventSource.CLOSED
    updateConnectionStatus('Disconnected', false);
  }
}, false);
*/