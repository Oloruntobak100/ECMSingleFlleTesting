if( typeof( $.fn.modal ) !== 'undefined' ){
	$.fn.modal.Constructor.prototype.enforceFocus = function () {};
}

(function($) {
    $.fn.cProcessForm = {
        api:0,
        api_get_params:'',
		customURL: "",
		requestURL: "",
		activateAjaxRequestButton: function(){
			
			var s = document.location.href.split("/sign-in/");
			$.fn.cProcessForm.requestURL = s[0] + "/engine/";

			if( $.fn.cProcessFormUrl && $.fn.cProcessFormUrl.requestURL ){
				$.fn.cProcessForm.requestURL = $.fn.cProcessFormUrl.requestURL;
				if( $.fn.cProcessFormUrl.customURL ){
					$.fn.cProcessForm.customURL = 1;
				}
			}
			
			$.fn.cProcessForm.bind_quick_print_function();
			
			$("body")
			.prepend("<style>.stay-opened .dropdown-menu.hold-on-click{display:block;}</style>");
			
			$("body")
			.on( "click", ".btn-group .dropdown-menu.hold-on-click select", function(e){
				if( $(this).parents(".btn-group.stay-opened") && ! $(this).parents(".btn-group.stay-opened").hasClass("open") ){
				  $(this).parents(".btn-group.stay-opened").addClass("open");
				}
				
				if( $(this).parents(".btn-group.open") && ! $(this).parents(".btn-group.open").hasClass("stay-opened") ){
					$(this).parents(".btn-group.open").addClass("stay-opened");
				}
			})
			.on( "blur", ".btn-group .dropdown-menu.hold-on-click select", function(e){
				if( $(this).parents(".btn-group.stay-opened") ){
				  $(this).parents(".btn-group.stay-opened").addClass("open stay-opened-blur").removeClass("stay-opened");
				}
			}) 
			.on( "click", '.dropdown-menu.hold-on-click input[type="submit"]', function(e){
				if( $(this).parents(".btn-group.stay-opened") ){
				  $(this).parents(".btn-group.stay-opened").removeClass("stay-opened");
				}
				
				if( $(this).parents(".btn-group.open") ){
				  $(this).parents(".btn-group.open").removeClass("open");
				}
			});
			
			$("body")
			.on( "click", ".ajax-request", function(e){
				e.preventDefault();
				
				if( $(this).hasClass("ajax-request-modals") ){
					$(this).attr("href", $(this).attr("data-href") );
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
					$('input.' + child).prop("checked", true ).change();
				}else{
					$('input.' + child).prop("checked", false ).change();
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
				$.fn.cProcessForm.buttonClickRequest( $(this), e );
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
			
			//$.fn.cProcessForm.attachDevTool();
		},
		attachDevTool: function(){
			if(window.attachEvent) {
				if (document.readyState === "complete" || document.readyState === "interactive") {
				  $.fn.cProcessForm.detectDevTool();
				  window.attachEvent('onresize', $.fn.cProcessForm.detectDevTool);
				  window.attachEvent('onmousemove', $.fn.cProcessForm.detectDevTool);
				  window.attachEvent('onfocus', $.fn.cProcessForm.detectDevTool);
				  window.attachEvent('onblur', $.fn.cProcessForm.detectDevTool);
				} else {
					setTimeout(argument.callee, 0);
				}
			}else{
				window.addEventListener('load', $.fn.cProcessForm.detectDevTool);
				window.addEventListener('resize', $.fn.cProcessForm.detectDevTool);
				window.addEventListener('mousemove', $.fn.cProcessForm.detectDevTool);
				window.addEventListener('focus', $.fn.cProcessForm.detectDevTool);
				window.addEventListener('blur', $.fn.cProcessForm.detectDevTool);
			}
			
		},
		setDetectDevTool: true,
		detectDevTool: function(allow) {
			setTimeout(function(){
			    //if(isNaN(+allow)) 
				var allow = 10;
				//if( $.fn.cProcessForm.setDetectDevTool ){
					var start = +new Date();
			      console.log(allow, start);
					debugger;
					var end = +new Date();
			      console.log(allow, end);
					if(isNaN(start) || isNaN(end) || end - start > allow) {
						document.write('&nbsp;');
					}
				//}
			}, 200);
		},
        buttonClickRequest: function( $me, e ){
			if( ! $me.attr('allow-default') ){
				e.preventDefault();
			}
			
			if( $me.attr('stop-propagation') ){
				e.stopPropagation();
			}
			
			var single_selected_record = "";
			
			var store = "";
			/* if( $('#current-store-container').find("input#current-store") && $('#current-store-container').find("input#current-store").val() ){
				store = $('#current-store-container').find("input#current-store").val();
			} */
			//$.fn.cProcessForm.activateProcessing( $me );
			
			if( $me.attr("override-selected-record") ){
				single_selected_record = $me.attr("override-selected-record");
			}
			
			if( ( ! single_selected_record  ) && $me.attr("selected-record") ){
				single_selected_record = $me.attr("selected-record");
			}
			
			var ids = '';
			if( $me.attr('use-checkbox') ){
				ids = $('input.'+ $me.attr('use-checkbox') +':checked').map(function() {return this.value;}).get().join(':::');
				
				if( ! ids ){
					var data = {theme:'alert-info', err:'No Record Selected', msg:'You must select items by clicking on the checkboxes first', typ:'jsuerror' };
					$.fn.cProcessForm.display_notification( data );
					return false;
				}
				
			}
			
			if( single_selected_record || ids ){
				var ok = 1;
				if( $.fn.cProcessForm.confirmPromptOk ){
					$.fn.cProcessForm.confirmPromptOk = 0;
				}else if( $me.attr('confirm-prompt') ){
					//ok = confirm( 'Are you sure that you want to ' + $me.attr('confirm-prompt') );
					return $.fn.cProcessForm.sendConfirmPrompt( 'Are you sure that you want to ' + $me.attr('confirm-prompt'), $me, e );
				}
				
				if( ok ){
					var module_id = "";
					var url = $me.attr('action');
					/* if( ! $me.attr('no-store') ){
						url += "&store=" + store;
					} */
					var dc = {};
					dc[ "mod" ] = $me.attr('mod');
					dc[ "id" ] = single_selected_record;
					dc[ "ids" ] = ids;
					var dj = $me.data('json');
					
					if( typeof( dj ) === "object" ){
						dc[ "bjson" ] = JSON.stringify( dj );
					}else if( dj ){
						dc[ "bdata" ] = dj;
					}
					var nt = '';
					if( $me.attr('new_tab') ){
						nt = $me.attr('new_tab');
					}
					var nt1 = '';
					if( $me.attr('new_title') ){
						nt1 = $me.attr('new_title');
					}else if( $me.attr('title') ){
						nt1 = $me.attr('title');
					}
					
					$.fn.cProcessForm.ajax_data = {
						ajax_data: dc,
						form_method: 'post',
						ajax_data_type: 'json',
						ajax_action: 'request_function_output',
						ajax_container: '',
						ajax_get_url: url,
						ajax_target: $me.attr('target'),
						ajax_new_tab: nt,
						ajax_title: nt1,
					};
					if( single_selected_record == "json" ){
						$.fn.cProcessForm.ajax_data.ajax_data.json = $("body").data("json");
					}
					if( $me.attr('auto_refresh') ){
						$.fn.cProcessForm.ajax_data.auto_refresh = $me.attr('auto_refresh');
					}
					
					//06-apr-23
					$.fn.cProcessForm.activateProcessing( $me );
					$.fn.cProcessForm.ajax_send();
					if( $me.hasClass("nwp_silent_req") ){
						$.fn.cProcessForm.function_click_process = 1;
					}

					if( $me.hasClass("fade-on-click") ){
						$me.css('opacity', 0.4);
					}
					
					if( $me.hasClass("one-time-request") ){
						$me.attr('action', '');
						$me.removeClass('custom-single-selected-record-button');
					}
				}
				
			}else{
				var data = {theme:'alert-info', err:'No Selected Record', msg:'Please select a record by clicking on it', typ:'jsuerror' };
				$.fn.cProcessForm.display_notification( data );
			}
			
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
			if( ! $button.hasClass("skip-processing") ){
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
						.attr("data-tmp", $button.html() )
						.css( "opacity", 0.3 )
						.text( "Please Wait..." );
					}
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
					.html( $button.attr("data-tmp") );
				}
				$button.removeClass("processing-ajax-request");
			}
		},
        post_form_data: function( $form ){
			
            if( $form.data('do-not-submit') != 'submit' ){
				return false;
			}
			
			// var fm_ser = $form.serialize();
			var fm_ser = $form.serializeAndEncode();
			var fm_ser2 = $.fn.cProcessForm.nw_serialize( fm_ser );

			var varx = 0;
			var allObj = [];
			var hasObjects = 0;

			$form
			.find('.nw-database_objects')
			.each(function(){
				hasObjects = 1;
				
				var d = {};
				var df = {};
				var $textarea = $(this).find('textarea.nw-database_objects-store');
				
				allObj.push( $(this).attr( 'data-field' ) );
				var df1 = JSON.parse( $textarea.val() );
				if( df1["data"] ){
					df = df1["data"];
				}
				/*
				if( df1["values"] ){
					d = df1["values"];
				}
				*/
				
				$(this).find('.form-gen-element,.form-check-input')
				.each(function(){
					
					var v = $(this).val();
					var nid = $(this)[0].hasAttribute( 'data-nid' ) ? $(this).attr('data-nid') : '';

					if( $(this).attr( 'type' ) == 'checkbox' && ! $(this).prop( 'checked' ) ){
						v = '';
					}else if( $(this).attr( 'type' ) == 'radio' && ! $(this).is( ':checked' ) ){
						v = '';
					}
					
					if( $(this).hasClass('auto-num-active') ){
						v = v.replace( /,/g, '' );
					}
					
					if( v ){
					// if( nid ){

						if( $(this).attr( 'type' ) == 'checkbox' && ! $(this).prop( 'checked' ) ){
							v = '';
						}
						
						if( $(this).hasClass('auto-num-active') ){
							v = v.replace( /,/g, '' );
						}
						
						if( ! d[ $(this).attr('data-nid') ] ){
							d[ $(this).attr('data-nid') ] = {};
						}

						if( ! d[ $(this).attr('data-nid') ][ $(this).attr('data-nfield') ] ){
							d[ $(this).attr('data-nid') ][ $(this).attr('data-nfield') ] = {};
						}
						
						if( ! d[ $(this).attr('data-nid') ][ $(this).attr('data-nfield') ][ $(this).attr('data-nkey') ] ){
							d[ $(this).attr('data-nid') ][ $(this).attr('data-nfield') ][ $(this).attr('data-nkey') ] = {};
						}
						
						if( $(this).attr( 'type' ) == 'checkbox' && typeof d[ $(this).attr('data-nid') ][ $(this).attr('data-nfield') ][ $(this).attr('data-nkey') ][ $(this).attr('data-key') ] !== 'undefined' ){
							v = d[ $(this).attr('data-nid') ][ $(this).attr('data-nfield') ][ $(this).attr('data-nkey') ][ $(this).attr('data-key') ] + ':::' + v;
						}

						d[ $(this).attr('data-nid') ][ $(this).attr('data-nfield') ][ $(this).attr('data-nkey') ][ $(this).attr('data-key') ] = v;
						
						if( v ){
							// console.log( $(this).attr( 'name' ) );
							// console.log( v );
						}

						if( $(this).attr('name') ){
							var nmm = $(this).attr('name');
							varx = 1;
							if( fm_ser2[ $(this).attr('name') ] ){
								delete fm_ser2[ nmm ];
							}
						}

					}
				});
				
				
				var d1 = {
					data: df,
					values: d,
				};

				$textarea.val( JSON.stringify( d1 ) );
				if( varx ){
					fm_ser2[ $textarea.attr( 'name' ) ] = $textarea.val();
				}
			});
			
			$(this).find('.form-gen-element,.form-check-input')
			.each(function(){
				
				var v = $(this).val();
				if( $(this).hasClass('auto-num-active') ){
					v = v.replace( /,/g, '' );
				}
				
				if( v ){
				// if( nid ){

					if( $(this).attr( 'type' ) == 'checkbox' && ! $(this).prop( 'checked' ) ){
						v = '';
					}
					
					if( $(this).hasClass('auto-num-active') ){
						v = v.replace( /,/g, '' );
					}
					
					if( ! d[ $(this).attr('data-nid') ] ){
						d[ $(this).attr('data-nid') ] = {};
					}

					if( ! d[ $(this).attr('data-nid') ][ $(this).attr('data-nfield') ] ){
						d[ $(this).attr('data-nid') ][ $(this).attr('data-nfield') ] = {};
					}
					
					if( ! d[ $(this).attr('data-nid') ][ $(this).attr('data-nfield') ][ $(this).attr('data-nkey') ] ){
						d[ $(this).attr('data-nid') ][ $(this).attr('data-nfield') ][ $(this).attr('data-nkey') ] = {};
					}
					
					if( $(this).attr( 'type' ) == 'checkbox' && typeof d[ $(this).attr('data-nid') ][ $(this).attr('data-nfield') ][ $(this).attr('data-nkey') ][ $(this).attr('data-key') ] !== 'undefined' ){
						v = d[ $(this).attr('data-nid') ][ $(this).attr('data-nfield') ][ $(this).attr('data-nkey') ][ $(this).attr('data-key') ] + ':::' + v;
					}

					d[ $(this).attr('data-nid') ][ $(this).attr('data-nfield') ][ $(this).attr('data-nkey') ][ $(this).attr('data-key') ] = v;
					
					if( v ){
						// console.log( $(this).attr( 'name' ) );
						// console.log( v );
					}

					if( $(this).attr('name') ){
						var nmm = $(this).attr('name');
						varx = 1;
						if( fm_ser2[ $(this).attr('name') ] ){
							delete fm_ser2[ nmm ];
						}
					}

				}
			});
			
			var params = '';
			if( varx ){
				$.each( fm_ser2, function( k, v ){
					if( k.substring(0,12) == 'nw_signature' ){
						params += k+'='+ v +'&';
					}else{
						var checkMultiple = k.substr( k.length - 6 );
						var bracesChar = '%5B%5D';
						if( checkMultiple == bracesChar ){
							var ex = v.split( "," );

							$.each( ex, function( k1, v1 ){
								params += k+'='+ encodeURIComponent( v1 ) +'&';

							});
						}else{
							params += k+'='+ encodeURIComponent( v ) +'&';
						}
					}
					// console.log( k, v );
				});
			}else{
				params = fm_ser;
			}

			if( hasObjects ){
				var pairs = params.split('&');

				// Step 3-4: Create the object
				var obj = {};
				pairs.forEach(function(pair) {
					pair = decodeURIComponent( pair );
					var parts = pair.split('=');
					switch( $.inArray( parts[0], allObj ) !== -1 ){
					case true:
					break;
					default:
						parts[1] = parts[1];
					break;
					}
					obj[ parts[0] ] = pair.replace( new RegExp( parts[0]+'=', 'g'), '');
				});
				params = obj;
			}

			// params = $.deparam( params, true );

			// console.log( params );
            $.fn.cProcessForm.ajax_data = {
                ajax_data: params,
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
				var sel = id;
				
				switch( action ){
				case "remove":
					var todo = 'remove_database_object';
				break;
				default:
					sel = 'nw-obj-button-'+id;
				break;
				}
				
				$.fn.cProcessForm.ajax_data = {
					ajax_data: { current_value: cText, data_field:$table.attr('data-field'), data_nfield:$table.attr('data-nfield'), data_nid:$table.attr('data-nid') },
					form_method: 'post',
					ajax_data_type: 'json',
					ajax_action: 'request_function_output',
					ajax_container: '',
					ajax_get_url: "?action=database_objects&todo=" + todo + "&html_replacement_selector="+sel,
				};
				$.fn.cProcessForm.ajax_send();
			}
		},
		nwp_page_title:'',
        nwp_hash: '',
        client_note_time: 0,
        modalWindow: null,
        pendingReload: 0,
        function_click_process: 1,
        specificAutonumeric: 0,
        callbacks: [],
        ajax_send: function( settings ){
            //Send Data to Server
            
            if( $.fn.cProcessForm.function_click_process ){
				
				if( $("body").hasClass("modal-open") ){
					if( $(".modal-dialog").is(":visible") ){
						if( $.fn.cProcessForm.ajax_data.ajax_get_url )$.fn.cProcessForm.ajax_data.ajax_get_url += "&modal=1";
						else $.fn.cProcessForm.ajax_data.ajax_get_url = "?modal=1";
					}else{
						$("body").removeClass("modal-open");
					}
				}
				
				var url = $.fn.cProcessForm.requestURL + 'php/ajax_request_processing_script.php' + $.fn.cProcessForm.ajax_data.ajax_get_url;
				
				// url += "&XDEBUG_SESSION_START=sublime.xdebug";
				
				if( $.fn.cProcessForm.customURL ){
					url = $.fn.cProcessForm.requestURL + $.fn.cProcessForm.ajax_data.ajax_get_url;
				}else if( $.fn.cProcessForm.api ){
					url = $.fn.cProcessForm.requestURL + 'api/' + $.fn.cProcessForm.ajax_data.ajax_get_url;
					if( $.fn.cProcessForm.api_get_params ){
						url += $.fn.cProcessForm.api_get_params;
					}
				}
				
				if( $.fn.cProcessForm.nwp_hash ){
					if( $.fn.cProcessForm.ajax_data.form_method == 'get' && ! $.isEmptyObject( $.fn.cProcessForm.ajax_data.ajax_data ) ){
						$.fn.cProcessForm.ajax_data.ajax_data.nwp_hash = $.fn.cProcessForm.nwp_hash;
					}else{
						url += '&nwp_hash=' + $.fn.cProcessForm.nwp_hash;
					}
				}
				
				if( $( 'body' ).attr( 'ispopup' ) ){

				}else if( $.fn.cProcessForm.ajax_data.ajax_target && $.fn.cProcessForm.ajax_data.ajax_target == '_blank' ){
					
					var tt = '';
					if( $.fn.cProcessForm.ajax_data.ajax_title ){
						tt = $.fn.cProcessForm.ajax_data.ajax_title;
					}
					url += '&pdata=' + encodeURIComponent( JSON.stringify( $.fn.cProcessForm.ajax_data.ajax_data ) );
					var docHeight = window.innerHeight;
					if( docHeight < 620 ){
						docHeight = 620;
					}

					var ispopup = 0;
					
					if( $.fn.cProcessForm.ajax_data.ajax_new_tab && $.fn.cProcessForm.ajax_data.ajax_new_tab != '2' ){
						var x = window.open();
					}else{
						ispopup = 1;
						var x = window.open("", "reportWindow", 'toolbar=no, location=no, menubar=no, resizable=yes, height='+docHeight+', width=1200');
						
						if( $.fn.cProcessForm.ajax_data.ajax_new_tab == '2' ){
							$.fn.cProcessForm.modalWindow = x;
							/* x.addEventListener('beforeunload', function (e) {
								$.fn.cProcessForm.modalWindow = 0;
							}); */
						}
					}
					
					x.document.open();
					
					x.document.write("<html><head><title>"+ tt +"</title></head><body style='margin:0; overflow:hidden; padding:0;' ispopup='"+ ispopup +"'><iframe src='"+ url +"&nwp_target=1&ispopup="+ ispopup +"' style='width:100%; height:100vh; border:0px;' id='frame' ds></iframe></body></html>");
					x.document.close();
					
					if( $.fn.cProcessForm.ajax_data.auto_refresh ){
						$.fn.cProcessForm.modalWindow = x;
						$.fn.cProcessForm.newWindowOpen( $.fn.cProcessForm.ajax_data );
					}
					//06-apr-23
					$.fn.cProcessForm.deactivateProcessing();
					return 1;
				}
				
				if( $.fn.cProcessForm.ajaxCanBeCancelled > 0 ){
					$.fn.cProcessForm.ajaxCanBeCancelled = -1;
					$.fn.cProcessForm.ajaxReq.abort();
				}

				//don't why we blocked it - 31-jul-22
				var now = new Date().getTime();
				/* console.log( 'a', $.fn.cProcessForm.client_note_time );
				console.log( 'now', now - 0 );
				console.log( 'diff', ($.fn.cProcessForm.client_note_time > 0 && $.fn.cProcessForm.client_note_time < now)?'t':'f' ); */
				
				if( $.fn.cProcessForm.ajax_data.nwp_notification || ( $.fn.cProcessForm.client_note_time > 0 && $.fn.cProcessForm.client_note_time < now ) ){
          			var nstd = $.fn.cProcessForm.localStore( 'notification_id', '', {}, 'get' );
					if( typeof $.fn.cClientNotification !== 'undefined' && nstd ){
                    	$.fn.cProcessForm.ajax_data.ajax_data[ 'nwp_client_notification' ] = nstd;
						$.fn.cProcessForm.client_note_time = now + 60000;
					}					
				}
				
				
                $.fn.cProcessForm.ajaxReq = $.ajax({
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
						if( $.fn.cProcessForm.ajaxCanBeCancelled == -1 ){
						   $.fn.cProcessForm.ajaxCanBeCancelled = 0;
						}else{
							$.fn.cProcessForm.function_click_process = 1;
							$.fn.cProcessForm.requestRetryCount = 0;
							$.fn.cProcessForm.ajaxError.call( event, request, settings, ex );
							$.fn.cProcessForm.deactivateProcessing();
						}
						
						if( event.responseText ){
							return $.fn.cProcessForm.display_notification( {theme:'alert-danger', err:'Message from Server', msg:event.responseText, typ:'jsuerror' } );
						}
                    },
                    success: function(data){
                        $.fn.cProcessForm.requestRetryCount = 0;
                        $.fn.cProcessForm.function_click_process = 1;
						$.fn.cProcessForm.deactivateProcessing();
                        
						if( data && Object.getOwnPropertyNames( data ).length && typeof data === 'object' && data.status ){
							
							if( data.nwp_hash ){
								$.fn.cProcessForm.nwp_hash = data.nwp_hash;
							}
							
							if( data.activate_open_new_tab ){
								if( $.fn.cProcessForm.activateNewTab < 0 ){
									$.fn.cProcessForm.activateNewTab = data.activate_open_new_tab;
								}
							}
							
							if( typeof(data.specific_autonumeric) !== 'undefined' ){
								$.fn.cProcessForm.specificAutonumeric = data.specific_autonumeric;
							}
							
							$.fn.cProcessForm.returned_ajax_data = data;
							if( data.data && data.data.nwp_client_notification_data && data.data.nwp_client_notification_data.id ){
								if( typeof $.fn.cClientNotification !== 'undefined' ){
									$.fn.cClientNotification.olddata = $.fn.cClientNotification.data;
									$.fn.cClientNotification.data = data.data.nwp_client_notification_data;
									$.fn.cClientNotification.data.base_url = $.fn.cProcessForm.requestURL;
									$.fn.cClientNotification.data.url = $.fn.cClientNotification.data.base_url+'php/sse.php';
									
									$.fn.cClientNotification.init();
									// setTimeout(function(){
									// },1000);
								}
							}
							
							if( data.data && data.data.pending_reload ){
								$.fn.cProcessForm.pendingReload = data.data.pending_reload;
								//used in edms home page
							}
							
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
				        				$.fn.cProcessForm.localStore( data.log_user_id, data.data.user_data.id, {}, 'put' );
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
									
									if( data.html_remove_class_selector && data.html_remove_class ){
										$(data.html_remove_class_selector)
										.removeClass( data.html_remove_class );
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
									
									if( data.html_insertafter_selector && data.html_insertafter ){
										$( data.html_insertafter )
										.insertAfter( data.html_insertafter_selector );
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
									
									if( data.empty_select2 ){
										if( $(data.empty_select2) ){
											$(data.empty_select2).select2("val", "");
										}
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
										try{
											$.each( cc , function( key, value ){
												if( value ){
													eval( value + "()" );
												}
											} );
										}catch{
											console.log( "JS Lib was not loaded", cc);
										}
									}
									
									if(data.saved_record_id){
										single_selected_record = data.saved_record_id;
									}

								}
							break;
							case "reload-page":
								//console.log('x', document.location);
								if( document.location && document.location.origin ){
									var href = document.location.origin;
									if( document.location.pathname ){
										href += document.location.pathname;
									}
									document.location = href;
								}else{
									document.location = document.location;
								}
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
							
							if( data.log_history && data.log_history.data ){
								$.fn.cProcessForm.userHistory( { 'data' : data.log_history.data, 'type' : 'save' } );
							}
							
							//11-jan-23
							if( data.page_title ){
								if( ! $.fn.cProcessForm.nwp_page_title ){
									$.fn.cProcessForm.nwp_page_title = ' - ' + $('title').text();
								}
								$('title').html( data.page_title + $.fn.cProcessForm.nwp_page_title );
							}
							
							$.fn.cProcessForm.activateOpenNewTab(0);
							
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
		activateOpenNewTab: function( actNow ){
			if( $.fn.cProcessForm.activateNewTab > 0 || actNow > 0 ){
				// alert();
			//setTimeout(function(){
					//, a.jstree-anchor ::disabled on 8-oct-22 by pato
				$(".custom-single-selected-record-button,.custom-action-button-old,.custom-multi-selected-record-button,.custom-single-selected-record-button-old, .custom-action-button")
				.not(".activatedNewTab")
				.on("contextmenu", function( event ){
					event.stopPropagation();
					var action = $(this).attr( 'action' );
					//console.log(900);
					if( $(this).hasClass( 'jstree-anchor' ) ){
						action = $(this).attr( 'id' ).includes(':::');

						if( action ){
							action = '?'+$(this).attr( 'id' ).replaceAll( ':::', '&' );
						}
					}else if( $(this).hasClass( 'activated-click-event' ) ){
						action = $(this).attr( 'href' );
					}
						//console.log( action );

					if( action ){
						var id = $(this).attr( 'override-selected-record' );
						var y = confirm( ( $(this).attr("title")?$(this).attr("title"):$(this).text() ) +"\n\nOpen in a new tab?" );
						
						if( y ){
							//console.log(action);
							// console.log( $(this).attr( 'action' ) );
							var x = window.open();
							
							var head = $( 'head' ).html();
							var preBody = '\
								<div id="nwp-connection" style="position: fixed;bottom:0;left:0;z-index:99999;"></div>\
								<div id="nwp-audio" style="position: fixed;bottom:0;left:0;z-index:99999;"></div>\
								<div id="notification-container" style="max-width:300px;"></div>\
								<div id="generate-report-progress-bar"></div>';

							var body = $( '#mis-container' ).clone().html();

							body = body.replaceAll( /type="text\/javascript">/g, 'type="text/javascript" hello="1">setTimeout(function(){');
							body = body.replaceAll( /type="text\/javascript" class="auto-remove">/g, 'type="text/javascript" hello="1">setTimeout(function(){');
							body = body.replaceAll( /<script>/g, '<script>setTimeout(function(){');
							body = body.replaceAll( /<\/script>/g, '},555);</script>');

							body = body.replaceAll( /activatedNewTab/g, '' );

							body = '<div id="mis-container">'+ body +'</div>';

							//var script = '<script>console.log("mike")</script>';
							var script = '';

							$('html').find( 'script' ).each( function(e,obj){
								var src = $(obj).attr( 'src' );
								if( src && ( src.substring( 0, 5 ) == 'asset' || src.substring( 0, 5 ) == 'hospi' ) ){
									script += '<script src="'+ src +'" ></script>';
								}
							});

							script += '<script>'+ $( 'script#nwp-home-script' ).html() +'</script>';

							script += "\
							<script>\
								console.log( $( '.hyella-source-container' ).attr( 'hyella-source' ) );\
								App.reHandleSidebarMenu();\
								$.fn.cProcessForm.activateAjaxRequestButton();\
								nwTreeView.refresh_tree_view();\
								$.fn.cProcessForm.activateOpenNewTab(1);\
								nwResizeWindow.resizeWindow();\
								$nwProcessor.set_function_click_event();\
								console.log(2);\
								$nwProcessor.get_properties_of_school();\
								$nwProcessor.bind_show_hide_column_checkbox();\
								$nwProcessor.bind_create_field_selector_control();\
								setTimeout(function(){\
									// prepare_new_record_form_new();\n\
									$.fn.cProcessForm.ajax_data = {\
										ajax_data: { id: '"+ id +"' },\
										form_method: 'post',\
										ajax_data_type: 'json',\
										ajax_action: 'request_function_output',\
										ajax_container: '',\
										ajax_get_url: '"+ action +"',\
									};\
									$.fn.cProcessForm.ajax_send();\
								},500)\
							</script>\
							";
							// console.log( script );

							var html = '<html><head>'+ head +'</head><body>'+preBody+body+'</body>'+ script +'</html>';
							
							x.document.write( html );
							window.stop();

						}
					}
				
				})
				.addClass("activatedNewTab");
			}
		},
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
				var y = prompt("Open File Directory", $(this).attr("hyella-source") );
				if( y ){
					$.fn.cProcessForm.ajax_data = {
						ajax_data: {file: $(this).attr("hyella-source") },
						form_method: 'post',
						ajax_data_type: 'json',
						ajax_action: 'request_function_output',
						ajax_container: '',
						ajax_get_url: "?action=database_table&todo=open_file",
					};
					$.fn.cProcessForm.ajax_send();
				}
			})
			.addClass("activated");
			
		},
		ajaxReq: '',
		activateNewTab: -1,
		ajaxCanBeCancelled: 0,
		triggerNewAjaxRequest: function(){
			if( ! $.isEmptyObject( $.fn.cProcessForm.ajax_data2 ) ){
				$.fn.cProcessForm.returned_ajax_data = $.fn.cProcessForm.ajax_data2;
				$.fn.cProcessForm.ajax_data2 = {};
			}
			
			$.fn.cProcessForm.ajax_data = {
				ajax_data: {mod: $.fn.cProcessForm.returned_ajax_data.mod, id:$.fn.cProcessForm.returned_ajax_data.id },
				form_method: 'post',
				ajax_data_type: 'json',
				ajax_action: 'request_function_output',
				ajax_container: '',
				ajax_get_url: $.fn.cProcessForm.returned_ajax_data.action,
			};
			$.fn.cProcessForm.ajax_data.ajax_get_url += '&nwp_repro=1';
			if( $.fn.cProcessForm.returned_ajax_data.nwp_silent ){
				$.fn.cProcessForm.ajax_data.ajax_get_url += '&nwp_silentr=' + $.fn.cProcessForm.returned_ajax_data.nwp_silent;
                var ud = $.fn.cProcessForm.localStore( 'user_details', {}, {}, 'get' );
                if( typeof ud.id != 'undefined' ){
					$.fn.cProcessForm.ajax_data.ajax_get_url += '&user_id=' + ud.id;
                }
                if( typeof ud.privilege != 'undefined' ){
					$.fn.cProcessForm.ajax_data.ajax_get_url += '&user_privilege=' + ud.privilege;
                }
			}
			$.fn.cProcessForm.ajax_send();
			//@nw4
			if( $.fn.cProcessForm.returned_ajax_data.nwp_silent ){
				$.fn.cProcessForm.ajaxCanBeCancelled = 1;
				$.fn.cProcessForm.function_click_process = 1;

				switch( $.fn.cProcessForm.returned_ajax_data.nwp_silent ){
				case 2:
					$.fn.cProcessForm.ajaxCanBeCancelled = 0;
				break;
				}
			}
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
			
			// Show request parameters both source and destination
			if( $.fn.cProcessForm.returned_ajax_data.development ){
				$('form.activate-ajax').each(function(){
					if( ! $(this).hasClass( 'skip_show_headers' ) ){
						var prev = $(this).prev();

						if( prev.hasClass( 'nw_form_params' ) ){
							prev.remove();
						}

						// console.log( $.fn.cProcessForm )
						var html = '';

						if( $(this)[0].hasAttribute( 'action' ) ){
							html += '<pre>Action: '+ $(this).attr( 'action' ) +'</pre>';
						}

						if( $.fn.cProcessForm.ajax_data && $.fn.cProcessForm.ajax_data.ajax_get_url ){
							html += '<pre>Origin: '+ $.fn.cProcessForm.ajax_data.ajax_get_url +'</pre>';
						}
						html = '\
						<div class="nw_form_params accordion">\
						  <details>\
						    <summary>-</summary>\
						    '+ html +'\
						  </details>\
						</div>';

						$(this).before( html );
					}
				});
			}
			
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
					var cx = "Are you sure you want to submit this form?";
					if( $(this).attr("confirm-prompt") ){
						cx = $(this).attr("confirm-prompt");
					}
					return $.fn.cProcessForm.sendConfirmPrompt( cx, $(this), e );
					
					/* var c = confirm( cx );
					if( ! c ){
						return false;
					} */
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
					
				}else if( $(this).attr('nwp-hide-class') ){
					$(this)
					.parents('form')
					.find( '.' + $(this).attr('nwp-hide-class') )
					.val('')
					.hide();
					
					$(this)
					.parents('form')
					.find( '.' + $(this).attr('nwp-hide-class') + '-' + $(this).val() )
					.show();
				}
			})
			.change();
			
			$.fn.cProcessForm.activateWordCount();
			//Activate Ajax file upload
			$.fn.cProcessForm.ajaxFileUploader();
			/* 
			 if ( jQuery().datepicker) {
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
			if( $.fn.cProcessForm.specificAutonumeric ){
				$('input[type="number"].auto-num-sel')
				.not(".auto-num-active")
				.addClass("auto-num-loading");
			}else{
				$('input[type="number"].auto-num-sel')
				.add('input[type="number"].form-gen-element')
				.not(".auto-num-active")
				.addClass("auto-num-loading");
			}
			
			
			if( typeof( AutoNumeric ) === 'function' ){
				if( $('input.auto-num-loading').length > 0 ){
					
					$('input.auto-num-loading')
					.each(function(e){
						
						if( $(this).attr('no-auto-num') ){
							return;
						}
						
						var mxt = [];
						var opt = {};
						var set = 0;
						var mx = 0;
						//opt.unformatOnSubmit = true;
						//opt.overrideMinMaxLimits = 'ignore';
						if( $(this).attr('step') == 'any' ){
							opt.alwaysAllowDecimalCharacter = true;
						}
						
						if( $(this).attr('max') ){
							mx = parseFloat( $(this).attr('max') * 1 );
							if( isNaN(mx) )mx = 0;
							if( mx > 999 ){
								set = mx;
								opt.maximumValue = mx;
								mxt.push( 'Max: ' + $.fn.cProcessForm.addComma( mx ) );
							}
						}else{
							set = 1;
							if( $(this).attr('data-o-type') ){
								opt.maximumValue = 9999999999999999;
							}else{
								opt.maximumValue = 2000000000;
							}
							//opt.minimumValue = 0;
						}
						
						if( set ){
							$(this).attr('type', 'text');
							if( $(this).attr('min') ){
								mx = parseFloat( $(this).attr('min') * 1 );
								if( isNaN(mx) )mx = 0;
								if( mx ){
									mxt.push( 'Min: ' + $.fn.cProcessForm.addComma( mx ) );
								}
								if( mx < 1 )mx = 0;
								opt.minimumValue = mx;
							}
							
							if( mxt.length > 0 ){
								$('<small class="auto-num-label"><i>'+ mxt.join(', ') +'</i></small>' ).insertAfter( $(this) );
							}
							
							// console.log( $(this).attr('name'), opt );
							new AutoNumeric( $( this ).get( 0 ), opt );
							//new AutoNumeric( $( this ).get( 0 ), 'dotDecimalCharCommaSeparator' );
						}
					});
					
					$('input.auto-num-loading')
					.removeClass("auto-num-loading")
					.addClass("auto-num-active");
				}
			}
			
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
					var $u = $(this);
					var $me = $(this).parent();
					var ini = 1;

					if( $(this).parents('.form-group.control-group.input-row').hasClass('calculated-item-con') ){
						//$u = $(this).parent().parent();
						ini = 0;
						$u = $(this).parents('.calculated-item-con.form-group.control-group.input-row').children(':first');
						$me = $u.parent();
					}else{
						if( $(this).parent().parent().hasClass('calculated-item-con') ){
							$u = $(this).parent().parent();
							$me = $(this).parent().parent().parent();
						}else{
							$u = $(this);
							$me = $(this).parent();
						}
					}
					
					
					$('<span class="pull-right" style="position: relative;"><a href="#" ref="input#'+$(this).attr("id")+'" class="clear-select2"><small>clear</small></a></span>')
					.insertBefore( $u );
					
					if( ini ){
						$me.css('display', 'initial');
					}
				}
				
				var action = $(this).attr("action");
				var minlength = 2;
				if( $(this).attr("minlength") )minlength = parseFloat( $(this).attr("minlength") * 1 );
				if( isNaN( minlength ) )minlength = 2;
				
				var form_id = '';
				var form_table_id = '';
				
				var $status_con = $(this).parents('.cell-element').find('.input-status');
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
				
				var surl = $.fn.cProcessForm.requestURL +'php/ajax_request_processing_script.php' + action;
				if( $.fn.cProcessForm.customURL ){
					surl = $.fn.cProcessForm.requestURL + action;
				}
				if( $.fn.cProcessForm.nwp_hash ){
					surl += '&nwp_hash=' + $.fn.cProcessForm.nwp_hash;
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
							var $me = $(this);
							switch( $me.attr('type') ){
							case "radio":
								$me = $(this).filter(":checked");
							break;
							}
							
							if( $me ){
								if( $me.attr("field_key") ){
									d[ $me.attr("field_key") ] = $me.val();
								}else{
									d[ $me.attr("name") ] = $me.val();
								}
							}
							
						} );
					  }
					  
					  return d;
					},
					results: function (data, page) { // parse the results into the format expected by Select2.
						// since we are using custom formatting functions we do not need to alter remote JSON data
						if( data.nwp_hash ){
							$.fn.cProcessForm.nwp_hash = data.nwp_hash;
						}
						
						var cc = [];
						if( data.input_status_type && data.input_status ){
							switch( data.input_status_type ){
							case 'prepend':
								$status_con.prepend( data.input_status );
							break;
							case 'append':
								$status_con.append( data.input_status );
							break;
							default:
								$status_con.html( data.input_status );
							break;
							}
						}
						
						if( data.javascript_functions && data.javascript_functions.length > 0 ){
							cc = data.javascript_functions;
						}
						
						if( cc.length > 0 ){
							$.fn.cProcessForm.returned_ajax_data = data;
							$.each( cc , function( key, value ){
								if( value )eval( value + "()" );
							} );
						}
						
						if( data.items ){
							return {
								results: data.items
							};
						}
					},
					cache: true
				  },
				  allowClear: true,
				  minimumInputLength: minlength,
				  initSelection: function (element, callback) {
					  
					  if( element.attr('tags') &&  element.attr('tags') == "true" ){
						if( $('textarea#' + element.attr('id') + '-option-array' ).length ){
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

						}else{
							if( element.val() && element.attr('label') ){
								var dv = element.val().split(",");
								var dl = element.attr('label').split(",");
								if( dv.length == dl.length ){
									callback( dv.map((e, i) => ({id: e, text: dl[i]})));
								}
							}
						}
					  }else{
						if( element.val() &&  element.attr('label') ){
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
			.addClass("active")
			.on('select2-opening', function (e) {
			  if( $(this).parents(".btn-group.open") ){
				  $(this).parents(".btn-group.open").addClass("stay-opened");
			  }
			   if( $(this).parents(".btn-group.stay-opened-blur") ){
				   $(this).parents(".btn-group.stay-opened-blur").addClass("open stay-opened").removeClass("stay-opened-blur");
			  }
			})
			.on('select2-close', function (e) {
			 if( $(this).parents(".btn-group.stay-opened") ){
				  $(this).parents(".btn-group.stay-opened").addClass("open").removeClass("stay-opened");
			  } 
			});
			
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

					$("."+name+"-replace").attr( 'data-value' , $(this).find('input').attr('data-value') );
					$("."+name+"-replace").attr( 'hide_on_select' , $(this).find('input').attr('hide_on_select') );
					$("."+name+"-replace").attr( 'name' , $(this).find('input').attr('name') );
					//$("."+name+"-replace").attr( 'id' , $(this).find('input').attr('id') );
					$("."+name+"-replace").attr( 'class' , $(this).find('input').attr('class') );
					$("."+name+"-replace").attr( 'alt' , $(this).find('input').attr('alt') );
					
					var surl = $.fn.cProcessForm.requestURL + 'php/upload.php';
					if( $.fn.cProcessForm.customURL ){
						surl = $.fn.cProcessForm.requestURL + 'upload.php';
					}
					
					var uploader = new qq.FileUploader({
						element: document.getElementById(id),
						directory:vdirectory,
						multiple:vmultiple,
						listElement: document.getElementById('separate-list'),
						action: surl,
						params: {
							nwp_hash: $.fn.cProcessForm.nwp_hash,
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
									var dm = responseJSON.element + '_json';
									
									//multiple upload
									if( multiple ){
										var i = $('input[name="'+responseJSON.element+'"]').val();
										if( i && i.length > 1 && i != 'none' )i = i + ":::" + responseJSON.stored_name;
										else i = responseJSON.stored_name;
										$('input[name="'+responseJSON.element+'"]').val( i );
									}else{
										$('textarea[name="'+ dm +'"]').val('');
										//single upload
										$('input[name="'+responseJSON.element+'"]').val( responseJSON.stored_name );
									}
									
									if( responseJSON.title && name_field ){
										if( ! $( 'input'+name_field ).val() ){
											$( 'input'+name_field ).val( responseJSON.title );
										}
									}
									
									
									var jd = {};
									var jdv = $('textarea[name="'+ dm +'"]').val();
									if( jdv ){
										var jd = JSON.parse( jdv );
										if( ! jd ){
											jd = {};
										}
									}
									
									//19-jun-23: 2echo
									var fpath = responseJSON.fullname + responseJSON.filename + "." + responseJSON.ext;
									if( responseJSON.fpath ){
										fpath = responseJSON.fpath.replace(/&amp;/g, '&');
										delete responseJSON.fpath;
									}
									
									jd[ md5( responseJSON.stored_name ) ] = responseJSON;
									$('textarea[name="'+ dm + '"]').val( JSON.stringify( jd ) );
									
									$.fn.cProcessForm.displayUploadedFile( dm, responseJSON.element );
									
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
											.attr( "src", fpath )
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
									
									if( ! multiple ){
										$('#upload-box-' + responseJSON.element ).find('ul.qq-upload-list').empty();
									}
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
		displayUploadedFile:function( dm, em ){
			
			if( $('textarea[name="'+ dm + '"]') ){
				
				
				var jd = {};
				var jdv = $('textarea[name="'+ dm +'"]').val();
				if( jdv ){
					var jd = JSON.parse( jdv );
					if( ! jd ){
						jd = {};
					}
				}
				
				var h = '';
				
				if( ! $.isEmptyObject( jd ) ){
					$.each(jd, function( jk, jv ){
						h += '<div class="file-upload-success" style="color:#ff6600; margin-bottom:4px;">'+ jv.oname +' <a id="uploaded-'+ jk +'" data-name="'+ em +'" data-stored_name="'+ jv.stored_name +'" href="#" class="pull-right btn btn-sm dark" title="Delete uploaded file" onclick="$.fn.cProcessForm.removeUploadedFile( '+ "'"+ jk +"'" +' );"><i class="icon-trash"></i></a></div>';
					});
				}
				
				$('#' + em + '-file-content' )
				.html( h );
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
		throw_notification:function ( data ){
			/*
			sample data
			$return[ 'title' ] = '<h4><strong>Instructions</strong><h4>';
			$return[ 'message' ] = $r[ 'data' ][ 'instruction' ];
			$return[ 'manual_close' ] = 1;
			$return[ 'theme' ] = 'alert-info';
			$return[ 'javascript_functions' ] = array( '$.fn.cProcessForm.throw_notification' );
			*/
			if( $.fn.cProcessForm.returned_ajax_data && $.fn.cProcessForm.returned_ajax_data.title && $.fn.cProcessForm.returned_ajax_data.message ){
				var data = {
					typ:'jsuerror',
					err:$.fn.cProcessForm.returned_ajax_data.title,
					msg:$.fn.cProcessForm.returned_ajax_data.message,
				};
				if( $.fn.cProcessForm.returned_ajax_data && $.fn.cProcessForm.returned_ajax_data.manual_close ){
					data.manual_close = 1;	
				}
				if( $.fn.cProcessForm.returned_ajax_data && $.fn.cProcessForm.returned_ajax_data.theme ){
					data.theme = $.fn.cProcessForm.returned_ajax_data.theme;
				}
				$.fn.cProcessForm.display_notification( data );
			}
		},
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
				case "deleted":
				case "serror":
					var html = '<div class="alert ' + theme + ' alert-block1 alert-dismissable">';
					  html += '<button type="button" class="close" id="alert-close-button" data-dismiss="alert">&times;</button>';
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
					.html( html );
					
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
					
					var cc = [];
					if( data.javascript_functions && data.javascript_functions.length > 0 ){
						cc = data.javascript_functions;
					}
					
					if( $.fn.cProcessForm.callbacks.length > 0 ){
						cc = cc.concat( $.fn.cProcessForm.callbacks );
					}
					
					if( cc.length > 0 ){
						tmp_data = data;
						$.fn.cProcessForm.returned_ajax_data = data;
						$.each( cc , function( key, value ){
							if( value )eval( value + "()" );
						} );
					}
					
				break;
				}
			}
			
			if( data && data.tok && $('form') ){
				$('form')
				.find('input[name="processing"]')
				.val( data.tok );
			}
			
			if( data && data.re_process && ! $.fn.cProcessForm.cancelAjaxRecursiveFunction ){
				$.fn.cProcessForm.ajax_data2 = data;
				$.fn.cProcessForm.triggerNewAjaxRequest();
			}
		},
		ajax_data2: {},
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
				
				var html2 = "";
				var html = "<br /><br /><strong>Form Fields with Invalid Data</strong><br />";
				var no = 0;
				$me
				.find('.invalid-data')
				.each(function(){
					++no;
					var ptext = $(this).parents(".input-group").find(".input-group-addon").text();
					if( ! ptext )ptext = $(this).parents(".control-group").find(".control-label").text();
					html += no + ". " + ptext + "<br />";
					
					if( $(this).attr('data-error-message') ){
						html2 += $(this).attr('data-error-message') + "<br />";
					}
				});
				
				$me.data('do-not-submit', '');
				if( html2 ){
					html += "<br /><br />" + html2;
				}
				
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
			case 'color':
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
			case 'decimal_long':
			case 'decimal':
			case 'currency':
				/*/[^0-9\-\.]/g*/
				data = ( data.replace( /,/g, "" ) );
				if( ! isNaN( parseFloat( data * 1 ) ) ){
					return 1;
				}
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
				
				var dlen = parseFloat( $('input#min-password-length').val() * 1 );
				if( isNaN( dlen ) )dlen = 6;
				
				if( ! $.fn.cProcessForm.pass ){
					//VERIFY PASSWORD
					if( data.length > dlen ){
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
			case 'time':
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
			
			$("body")
			.on( "click", "button#summary-view", function(e){
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
					var dx = {html:report_title + html + all_signatories_html, current_module:$('#active-function-name').attr('function-class'), current_function:$('#active-function-name').attr('function-id'), report_title:r_title, report_show_user_info:r_user_info , orientation:orientation, paper:paper, rfrom:rfrom, rto:rto, rref:rref, report_template:r_template, info:r_ainfo, direct_print:direct_print, exclude_header:exclude_header };
					
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
					
					// console.log( dx );
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
					// console.log( dx );
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
		
			if( container && $("#"+container) ){
				
				var img = $("#"+container).find("iframe").contents().find('input[name="image"]').val();
				var h = '';
				var clear_container = 1;
				
				if( img ){
					
					if( typeof captureType == 'string' ){
						if( captureType && $("input#id-"+captureType+"-sign") ){
							$("input#id-"+captureType+"-sign").val( img );
						}
						
						if( $("input[name='"+captureType+"']") ){
							$("input[name='"+captureType+"']").val( img );
							h = '<img src="'+ img +'" style="max-width:100%;" />';
						}else{
							var data = {theme:'alert-danger', err:'No Form Fund', msg:'Signature cannot be captured', typ:'jsuerror' };
							$.fn.cProcessForm.display_notification( data );
						}
					}else if( typeof captureType == 'object' && ! $.isEmptyObject( captureType ) && typeof captureType.action !== 'undefined' ){
						clear_container = typeof captureType.clear_container !== 'undefined' ? captureType.clear_container : clear_container;
						h = '<img src="'+ img +'" style="max-width:100%;" />';
						
						
						$.fn.cProcessForm.ajax_data = {
							ajax_data: {image: img },
							form_method: 'post',
							ajax_data_type: 'json',
							ajax_action: 'request_function_output',
							ajax_container: '',
							ajax_get_url: captureType.action,
						};
						$.fn.cProcessForm.ajax_send();
						
					}else{
						var data = {theme:'alert-info', err:'Invalid Capture Type', msg:'No signature was captured, to capture a signature click on the SAVE SIGNATURE button before you close the capture screen', typ:'jsuerror' };
						$.fn.cProcessForm.display_notification( data );
					}
				}else{
					var data = {theme:'alert-info', err:'No Signature Captured', msg:'No signature was captured, to capture a signature click on the SAVE SIGNATURE button before you close the capture screen', typ:'jsuerror' };
					$.fn.cProcessForm.display_notification( data );
				}
				
				if( clear_container )$("#"+container).html( h );
				
			}else{
				var data = {theme:'alert-danger', err:'No Signature Container', msg:'Signature cannot be captured', typ:'jsuerror' };
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
				// console.log(data);
				var gid = data.id;
				var item = data.item;
				var reference = data.reference;
				var public_key = data.public_key;
				var project_title = data.project_title;
				var project_logo = data.project_logo;
				var hr = '';
				var ca = '';
				var ua = '';
				if( data.update_action ){
					ua = data.update_action;
				}
				if( data.cancel_action ){
					ca = data.cancel_action;
				}
				if( data.html_replacement_selector ){
					hr = data.html_replacement_selector;
				}
				
				var amt = parseFloat(data.amount_paid * 1);
				if( amt > 0 ){
					//console.log("x", amt );
					if( public_key ){
					
						switch( data.payment_method ){
						case "rave":
							if( Math.floor( amt ) != amt ){
								edata = {theme:'alert-danger note note-danger', err:'Flutterwave does not accept decimal values', msg:'Please select a different payment method and try again', typ:'uerror' };
							}else{
								var modalFlutSuc = 0;
							 const modalFlut = FlutterwaveCheckout({
								public_key: public_key,
								//public_key: "FLWPUBK_TEST-61d1ddcecc9720bafe4b857df714d14a-X",
								//tx_ref: "hyella-coop-" + (Math.random().toString(36).substring(7) || Math.random().toString(36).substr(2, 5)) + '-' + data.id,
								tx_ref: data.id,
								amount: Math.floor( amt ),
								currency: data.currency.toUpperCase(), //"NGN",
								payment_options: data.payment_options, //"card,mobilemoney,ussd",
								customer: {
								  email: data.payer.email,
								  phonenumber: data.payer.phone,
								  name: data.payer.name,
								},
								onclose: function() {
									if( ! modalFlutSuc ){
										// close modal
										$('#' + hr ).html( 'Payment Cancelled' );
										
										d = { id : gid, 'reference' : reference, }
										$.fn.cProcessForm.ajax_data = {
											ajax_data: d,
											form_method: 'post',
											ajax_data_type: 'json',
											ajax_action: 'request_function_output',
											ajax_container: '',
											ajax_get_url: ca + "&html_replacement_selector=" + hr,
										};
										$.fn.cProcessForm.ajax_send();
									}
									
									//console.log('clo' + gid); alert(12);
								},
								callback: function (response) {
									//console.log(response); alert(2); //return false;
									modalFlutSuc = 1;
								  switch ( response.status ){
									case "successful":
									//case "completed":
										d = { id : gid, 'reference' : reference, 'payment_method' : 'rave', 'response' : JSON.stringify(response) }
										$.fn.cProcessForm.ajax_data = {
											ajax_data: d,
											form_method: 'post',
											ajax_data_type: 'json',
											ajax_action: 'request_function_output',
											ajax_container: '',
											ajax_get_url: ua + "&html_replacement_selector=" + hr,
										};
										$.fn.cProcessForm.ajax_send();
										
									break;
									case 'error':
										var edata = {theme:'alert-danger note note-danger', err:'Rave Payment Failed', msg:'Please try again', typ:'uerror' };
										return $.fn.cProcessForm.display_notification( edata );
									break;
									default:
										var edata = {theme:'alert-danger note note-danger', err:'Unknown Rave Payment Error', msg:'Please try again', typ:'uerror' };
										return $.fn.cProcessForm.display_notification( edata );
									break;
								  }
								  modalFlut.close();
								},
								customizations: {
								  title: data.payment_category,
								  description: project_title,
								  //description: "Hyella Cooperarive Module Payment",
								  logo: project_logo,
								  //logo: "http://localhost:819/coop/www/icon.png",
								},
							  });
							}
						break;
						case "pay_stack": //use this
							let handler = PaystackPop.setup({
								key: public_key,
								//key: 'pk_test_6ac473dd058d051a234dbfdc1025f3204a0384df', // Public key
								email: data.payer.email,
								amount:  amt * 100, // You have to multiply amount by 100 for it to work currectly
								firstname:  data.payer.first_name,
								lastname: data.payer.name,
								onClose: function(){
									//alert('Transaction Not completed. Window closed.');
									$('#' + hr ).html( 'Payment Cancelled' );
									
									d = { id : gid, 'reference' : reference, }
									$.fn.cProcessForm.ajax_data = {
										ajax_data: d,
										form_method: 'post',
										ajax_data_type: 'json',
										ajax_action: 'request_function_output',
										ajax_container: '',
										ajax_get_url: ca + "&html_replacement_selector=" + hr,
									};
									
									$.fn.cProcessForm.ajax_send();
								},
								callback: function(response){
									//console.log( response );
									switch(response.status){
									case "success":
										d = { id : gid, 'reference' : reference, payment_method : 'pay_stack', 'response' : JSON.stringify( response ) }
										// console.log( response );
										$.fn.cProcessForm.ajax_data = {
											ajax_data: d,
											form_method: 'post',
											ajax_data_type: 'json',
											ajax_action: 'request_function_output',
											ajax_container: '',
											ajax_get_url: ua + "&html_replacement_selector=" + hr,
										};
										$.fn.cProcessForm.ajax_send();
									break;
									default:
										d = response;
										d.id = gid; d.payment_method = 'pay_stack';
										$.fn.cProcessForm.ajax_data = {
											ajax_data: d,
											form_method: 'post',
											ajax_data_type: 'json',
											ajax_action: 'request_function_output',
											ajax_container: '',
											ajax_get_url: ua + "&html_replacement_selector=" + hr,
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
						edata = {theme:'alert-danger note note-danger', err:'Invalid Public Key', msg:'Please contact technical support team', typ:'uerror' };
					}
				}else{
					edata = {theme:'alert-danger note note-danger', err:'Invalid Payment Amount', msg:'Please select a different payment method and try again', typ:'uerror' };
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
				$("#"+$.fn.cProcessForm.selectElementID)
				.find(".hidden-print")
				.addClass("nwp-tmp-hide");
				
				$("#"+$.fn.cProcessForm.selectElementID)
				.find(".no-print")
				.addClass("nwp-tmp-hide");
				
				$("#"+$.fn.cProcessForm.selectElementID)
				.find(".nwp-tmp-hide").hide();
			
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
				
				$("#"+$.fn.cProcessForm.selectElementID)
				.find(".nwp-tmp-hide").show().removeClass("nwp-tmp-hide");
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
		circleProgress: function(selector){
			var wrapper = document.querySelectorAll(selector);
			Array.prototype.forEach.call(wrapper, function(wrapper, i) {
			  var wrapperWidth,
				wrapperHeight,
				percent,
				innerHTML,
				context,
				lineWidth,
				centerX,
				centerY,
				radius,
				newPercent,
				speed,
				from,
				to,
				duration,
				start,
				strokeStyle,
				text;

			  var getValues = function() {
				wrapperWidth = parseInt(window.getComputedStyle(wrapper).width);
				wrapperHeight = wrapperWidth;
				percent = wrapper.getAttribute('data-cp-percentage');
				
				var dtext = wrapper.getAttribute('data-text');
				var dwidth = parseInt( wrapper.getAttribute('data-line-width') );
				if( isNaN( dwidth ) )dwidth = 0;
				
				innerHTML = '<span class="percentage"><strong>' + percent + '</strong> %</span><canvas class="circleProgressCanvas" width="' + (wrapperWidth * 2) + '" height="' + wrapperHeight * 2 + '"></canvas>';
				
				if( dtext ){
					innerHTML = dtext + '<span class="percentage" style="display:none;"></span><canvas class="circleProgressCanvas" width="' + (wrapperWidth * 2) + '" height="' + wrapperHeight * 2 + '"></canvas>';
				}
				
				wrapper.innerHTML = innerHTML;
				text = wrapper.querySelector(".percentage");
				canvas = wrapper.querySelector(".circleProgressCanvas");
				wrapper.style.height = canvas.style.width = canvas.style.height = wrapperWidth + "px";
				context = canvas.getContext('2d');
				centerX = canvas.width / 2;
				centerY = canvas.height / 2;
				newPercent = 0;
				speed = 1;
				from = 0;
				to = percent;
				duration = 1000;
				lineWidth = 15;
				if( dwidth ){
					lineWidth = dwidth;
				}
				
				radius = canvas.width / 2 - lineWidth;
				strokeStyle = wrapper.getAttribute('data-cp-color');
				start = new Date().getTime();
			  };

			  function animate() {
				requestAnimationFrame(animate);
				var time = new Date().getTime() - start;
				if (time <= duration) {
				  var x = easeInOutQuart(time, from, to - from, duration);
				  newPercent = x;
				  text.innerHTML = Math.round(newPercent) + " %";
				  drawArc();
				}
			  }

			  function drawArc() {
				var circleStart = 1.5 * Math.PI;
				var circleEnd = circleStart + (newPercent / 50) * Math.PI;
				context.clearRect(0, 0, canvas.width, canvas.height);
				context.beginPath();
				context.arc(centerX, centerY, radius, circleStart, 4 * Math.PI, false);
				context.lineWidth = lineWidth;
				context.strokeStyle = "#ddd";
				context.stroke();
				context.beginPath();
				context.arc(centerX, centerY, radius, circleStart, circleEnd, false);
				context.lineWidth = lineWidth;
				context.strokeStyle = strokeStyle;
				context.stroke();

			  }
			  var update = function() {
				getValues();
				animate();
			  }
			  update();
			  var resizeTimer;
			  window.addEventListener("resize", function() {
				clearTimeout(resizeTimer);
				resizeTimer = setTimeout(function() {
				  clearTimeout(resizeTimer);
				  start = new Date().getTime();
				  update();
				}, 250);
			  });
			});

			//
			// http://easings.net/#easeInOutQuart
			//  t: current time
			//  b: beginning value
			//  c: change in value
			//  d: duration
			//
			function easeInOutQuart(t, b, c, d) {
			  if ((t /= d / 2) < 1) return c / 2 * t * t * t * t + b;
			  return -c / 2 * ((t -= 2) * t * t * t - 2) + b;
			}
		},
		nw_serialize: function( txt ){
			var r = {};
			if( txt ){
				var cl = txt.replace('?', '');
				if( cl ){
					var cl2 = cl.split('&');
					for( var i = 0; i < cl2.length; i++){
						var cl3 = cl2[ i ].split('=');
						if( cl3[0] ){
							var fval = cl3[1];
							var fkey = cl3[0];
							if( fval ){
								var checkMultiple = fkey.substr( fkey.length - 6 );
								if( checkMultiple == '%5B%5D' ){
									if( r[ cl3[0] ] ){
										r[ cl3[0] ] += ','+fval;
									}else{
										r[ cl3[0] ] = fval;
										// r[ cl3[0] ] = 'ThisIsMultiple'+fval;
									}
								}else{
									r[ cl3[0] ] = fval;
								}
							}else{
								r[ cl3[0] ] = '';
							}
						}
					} 
				}
			}
			return r;
		},
		nw_auto_load_link: function( params ){
			var try_one = 0;
			if( params && params.try_once ){
				try_one = 1;
			}else if( $.fn.cProcessForm.returned_ajax_data && $.fn.cProcessForm.returned_ajax_data.data && $.fn.cProcessForm.returned_ajax_data.data.try_one ){
				try_one = $.fn.cProcessForm.returned_ajax_data.data.try_one;
			}
			
			var ae = 'auto-load';
			if( params && params.auto_load_class ){
				ae = params.auto_load_class;
			}else if( $.fn.cProcessForm.returned_ajax_data && $.fn.cProcessForm.returned_ajax_data.data && $.fn.cProcessForm.returned_ajax_data.data.auto_load_class ){
				ae = $.fn.cProcessForm.returned_ajax_data.data.auto_load_class;
			}
			
			var $a = $( '.' + ae + ':first');
			if( $a.hasClass('custom-single-selected-record-button') ){
				var yx = { "post": { "id" : $a.attr('override-selected-record') }, "get": $.fn.cProcessForm.nw_serialize( $a.attr('action') ) };
			
				$.fn.cProcessForm.ajax_data = {
					ajax_data: { json: JSON.stringify( yx ) },
					form_method: 'post',
					ajax_data_type: 'json',
					ajax_action: 'request_function_output',
					ajax_container: '',
					ajax_get_url: "?action=reports&todo=auto_load_report",
				};
				if( try_one || $a.hasClass( ae + '-1' ) ){
					$a.removeClass( ae );
				}
				$.fn.cProcessForm.ajax_send();
				$.fn.cProcessForm.function_click_process = 1;
			}
		},
		historyLimit : 20,
		userHistory: function( params ){
			if( params.type ){
				var historyKey = 'history_data';
				var bp = $.fn.cProcessForm.localStore( historyKey, '', '', 'get' );
				
				switch( params.type ){
				case 'save':
					var updateHistoryKey = $.fn.cProcessForm.uniqueKey();
					var current_key = $.fn.cProcessForm.localStore( 'current_key', '', '', 'get' );
					if( ! bp )bp = {};


					var keys = Object.keys( bp ).sort();
					var loc = keys.indexOf( current_key );
					var total = Object.keys( bp ).length;
					// console.log( performance.getEntriesByType( 'navigation' ) );

					if( updateHistoryKey ){
						history.pushState( { page: updateHistoryKey }, "title "+updateHistoryKey, window.location.pathname );
					}

					if( loc !== total ){
						for( var i = loc+1 ; i < total ; i++ ){
							delete( bp[ keys[ i ] ] );
						}
					}

					if( total > $.fn.cProcessForm.historyLimit ){
						delete( bp[ keys[ 0 ] ] );
					}

					bp[ updateHistoryKey ] = params.data;
					bp[ updateHistoryKey ].key = updateHistoryKey;

					$.fn.cProcessForm.localStore( historyKey, bp, {}, 'put' );
					// save surrent_key
					// limit obj to 15
					$.fn.cProcessForm.localStore( 'current_key', updateHistoryKey, {}, 'put' );
				break;
				case 'use':
					// alert();

					if( ! $.isEmptyObject( bp ) ){

						var previous = 1;
						var next = 1;
						var load = {};

						// load = bp[ keys[ loc ] ];
						// console.log( params.event.state );

						if( params.event && params.event.state && params.event.state.page && bp[ params.event.state.page ] ){
							load = bp[ params.event.state.page ];
						}

						if( load.post && load.get ){
							load.get[ 'is_menu' ] = 0;
							var get = new URLSearchParams( load.get ).toString();
							$.fn.cProcessForm.ajax_data = {
								ajax_data: load.post,
								form_method: 'post',
								ajax_data_type: 'json',
								ajax_action: 'request_function_output',
								ajax_container: '',
								ajax_get_url: '?'+get,
								skip_unsaved: 1,
							};
							$.fn.cProcessForm.ajax_send();
							
							$.fn.cProcessForm.localStore( 'current_key', load.key, {}, 'put' );
							history.replaceState( { page: load.key }, null, null );
						}

					}

				break;
				}

			}
		},
		uniqueKey: function(){
			var launch_date = new Date();
			return 'd' + launch_date.getTime(); // + 'd' + Math.floor(Math.random() * (max - min + 1) + min);
		},
        modalClosed: function(){
			$("body").removeClass("modal-open");
		},
		printCanvas: function( a ){
			if( a && $('#' + a ).attr("data-canvas") ){
				var canvas_id = $('#' + a ).attr("data-canvas");
				var canvas_name = $('#' + a ).attr("data-name");
				
				if( ! canvas_name ){
					canvas_name = 'Download';
				}
				var canvas = document.getElementById( canvas_id );
				
				switch( $('#' + a ).attr("data-type") ){
				case "print":
					var t = '';
					if( canvas_name != 'Download' ){
						t = "<h2>"+ canvas_name +"</h2>";
					}
					var win = window.open();
					win.document.write( t + "<br><img src='" + canvas.toDataURL() + "'/>");
					
					win.document.addEventListener('load', function() {
						win.focus();
						win.print();
						win.document.close();
						win.close();            
					}, true);
					
				break;
				case "print_html":
					var t = $('head').html();
					var docHeight = window.innerHeight;
					if( docHeight < 620 ){
						docHeight = 620;
					}
					
					var win = window.open("", "reportWindow", 'toolbar=no, location=no, menubar=no, resizable=yes, height='+docHeight+', width=1200');
					
					/* win.document.write( "<head><title>"+ canvas_name +"</title>"+ t +"<style>.hidden-print,.no-print{ display:none; }</style></head><body>"+ $('#' + canvas_id ).html() +"</body>");
					
					setTimeout( function(){ win.print(); win.close();  }, 300 ); */
					
					win.document.write( "<head><title>"+ canvas_name +"</title>"+ t +"<style>.hidden-print,.no-print{ display:none; }</style></head><body>"+ $('#' + canvas_id ).html() +"</body><script>setTimeout( function(){ window.print(); }, 300 );</script>");
				break;
				case "save":
					var a = document.createElement('a');
					a.href = canvas.toDataURL("image/png").replace("image/png", "image/octet-stream");
					a.download = canvas_name + '.png';
					document.body.appendChild(a);
					a.click();
					a.remove();
				break;
				}
				
			}
		},
		reloadGoogleRecaptcha: function(){
			if( typeof( grecaptcha ) === "object" ){
				grecaptcha.reset();
			}
		},
		activateWordCount: function(){
			$('.form-control.nwp-word-count')
			.not('.nwp-word-count-active')
			.keydown( function( e ){
				//console.log(e.keyCode);
				var e1 = {};
				e1.ctrlKey = (e.ctrlKey)?1:0;
				e1.keyCode = e.keyCode;
				e1.type = 'keydown';
				var r = $.fn.cProcessForm.updateWordCount2( $(this), e1 );
				//console.log('d', r);
				if( r ){
					return false;
				}
			})
			.keyup( function( e ){
				var e1 = {};
				e1.ctrlKey = (e.ctrlKey)?1:0;
				e1.keyCode = e.keyCode;
				e1.type = 'keyup';
				var r = $.fn.cProcessForm.updateWordCount2( $(this), e1 );
				//console.log('u', r);
				if( r ){
					return false;
				}
			})
			.change( function(){
				var e = {};
				e.ctrlKey = 0;
				e.keyCode = 999001;
				e.type = 'change';
				$.fn.cProcessForm.updateWordCount2( $(this), e );
			}).change();
		},
		updateWordCount: function( $me, e ){
			//console.log( e );
			if( e.type && e.keyCode ){
				var f_name = $me.attr('name').replace('[','__').replace(']','__');
				
				get = 0;
				switch(e.type){
				case 'keyup':
					if( e.ctrlKey && e.keyCode == 86 ){
						get = 1;
					}
					
					switch(e.keyCode){
					case 8: //backspace
					case 46: //delete
						get = 1;
					break;
					}
				break;
				case 'change':
					if( ! $me.hasClass('nwp-word-count-active') ){
						$me.addClass('nwp-word-count-active');
						
						if( $me.attr('nwp-word-count') ){
							var c = parseFloat( $me.attr('nwp-word-count') * 1 );
							if( isNaN(c) ){
								c = 0;
							}
							if( c ){
								$('<div class="nwc nwc-'+ f_name +' text-danger"><small>Word Limit: <span class="nwc-count"></span>' + c +'</small></div>').insertAfter( $me );
							}
						}
					}
				case 'keydown':
					switch(e.keyCode){
					case 999001: //change
					case 13: //enter
					case 32: //space
					case 8: //backspace
					case 46: //delete
						get = 1;
					break;
					}
				break;
				}
				
				if( get ){
					var cia = $me.val().replace(/\n/g, ' ').replace('  ', ' ').trim().split(' ');
					var ci = cia.length;
					var c = parseFloat( $me.attr('nwp-word-count') * 1 );
					if( isNaN(c) ){
						c = 0;
					}
				}else{
					ci = 0;
					c = 0;
				}
				
				switch(e.type){
				case 'keyup':
					if( e.ctrlKey && e.keyCode == 86 ){
						if( c && ci && ci >= c ){
							$me.val( cia.splice(0, c ).join(' ') );
						}
					}
					
					switch(e.keyCode){
					case 8: //backspace
					case 46: //delete
						if( ! ( c && ci && ci >= c ) ){
							$me.removeClass('nwc-block');
						}
					break;
					}
				break;
				case 'change':
				case 'keydown':
				
					switch(e.keyCode){
					case 999001: //change
					case 13: //enter
					case 32: //space
					case 8: //backspace
					case 46: //delete
						
						switch(e.keyCode){
						case 13: //enter
						case 32: //space
						//case 999001: //change
							$me.removeClass('nwc-block');
						break;
						}
						
						if( ci && c > ci ){
							var add = 1;
							switch(e.keyCode){
							case 8: //backspace
							case 46: //delete
							case 999001: //change
								$me.removeClass('nwc-block');
								add = 0;
							break;
							}
							//correct later
							if( ci == 1 )ci = 0;
							
							$me
							.parents('form')
							.find( '.nwc-'+ f_name + ' .nwc-count' )
							.html( ( ci + add ) + ' of ' );
						}else if( ci && ci >= c ){
							
							switch(e.keyCode){
							case 13: //enter
							case 32: //space
							//case 999001: //change
								$me.addClass('nwc-block');
								return 1;
							break;
							}
							
						}else{
							$me.removeClass('nwc-block');
						}
					break;
					case 16: case 17: case 37: case 39: case 40: case 38:
					case 36: case 35: case 33: case 34:
					break;
					default:
						if( ! ( e.ctrlKey ) ){
							if( $me.hasClass('nwc-block') ){
								return 1;
							}
						}
					break;
					}
				
				break;
				}
			}
		},
		updateWordCount2: function( $me, e ){
			//console.log( e );
			if( e.type && e.keyCode ){
				var f_name = $me.attr('name').replace('[','__').replace(']','__');
				if( !($('.nwc-' + f_name).is(':visible')) ){
					if( ! $me.hasClass('nwp-word-count-active') ){
						$me.addClass('nwp-word-count-active');
						
						if( $me.attr('nwp-word-count') ){
							let c = parseFloat( $me.attr('nwp-word-count') * 1 );
							if( isNaN(c) ){
								c = 0;
							}
							if( c ){
								$('<div class="nwc nwc-'+ f_name +' text-danger"><small>Word Limit: <span class="nwc-count"></span>' + c +'</small></div>').insertAfter( $me );
							}
						}
					}
				}

				if( $('.nwc-' + f_name).is(':visible') ){

					let c = parseFloat( $me.attr('nwp-word-count') * 1 );
					c = isNaN( c ) ? 0 : c;
					let v = $me.val().replace(/\n|\s+/g, ' ').trim().split(' ');
					let b = v.length;
					v.forEach(i => {if( !i ) --b; } );

					if( e.ctrlKey && e.keyCode == 86 ){
						if( c && v && b > c ){
							$me.val( v.splice(0, c ).join(' ') );
							b = c;
							e.keyCode = 999001;
						}
					}
					switch(e.keyCode){
						case 999001: //change
						case 13: //enter
						case 32: //space
							if( b >= c ) $me.addClass('nwc-block');
						break;
						case 8: //backspace
						case 46: //delete
							if( $me.hasClass('nwc-block') )  $me.removeClass('nwc-block');
						break;

					}
					
					$me.parents('form').find( '.nwc-'+ f_name + ' .nwc-count' ).html( b + ' of ' );
					if( $me.hasClass('nwc-block') ) return 1;

		
				}
				

			}
		},
		newWindowOpen: function ( px ) {
			if( px.auto_refresh ){
				let siteTitle = document.title;
				 
				$(window).blur(function() {
				  document.title = 'Modal Window is Open !!!';
				});
				
				$(window).focus(function() {
					setTimeout(function(){
					if( $.fn.cProcessForm.modalWindow == null || $.fn.cProcessForm.modalWindow.closed ){
						
						$(window).off( 'blur' );
						$(window).off( 'focus' );
						document.title = siteTitle;
						if( $('#' + px.auto_refresh ).is(":visible") ){
							$('#' + px.auto_refresh ).click();
						}
					}else{
						var data = {theme:'note note-warning', err:'Modal Window is Open', msg:'Please close the modal window before you proceed with any action', typ:'jsuerror' };
						$.fn.cProcessForm.display_notification( data );
					}
					}, 500 );
				});
			}
		},
		fullScreen: function ( uk ) {
			if( $('#' + uk ).is(":visible") ){
				$('#' + uk ).toggleClass('nwp-full-screen');
				
				if( $('#' + uk ).hasClass('nwp-full-screen') ){
					$.fn.cProcessForm.localStore( 'nwp-full-screen', 1, {}, 'put' );
				}else{
					$.fn.cProcessForm.localStore( 'nwp-full-screen', 1, {}, 'delete' );
				}
			}
		},
		confirmPromptOk: 0,
        confirmPromptEvent: '',
        confirmPromptEl: '',
		confirmPromptYes: function(){
			$('#nwp-confirm-prompt-container').remove();
			$.fn.cProcessForm.confirmPromptOk = 1;
			
			switch( $.fn.cProcessForm.confirmPromptEl.get(0).localName ){
			case 'form':
				$.fn.cProcessForm.activateProcessing( $.fn.cProcessForm.confirmPromptEl.find("input[type='submit']") );
				$.fn.cProcessForm.post_form_data( $.fn.cProcessForm.confirmPromptEl );
			break;
			default:
				$.fn.cProcessForm.buttonClickRequest( $.fn.cProcessForm.confirmPromptEl, $.fn.cProcessForm.confirmPromptEvent );
			break;
			}

			$.fn.cProcessForm.confirmPromptOk = 0;
			$.fn.cProcessForm.confirmPromptEl = '';
			$.fn.cProcessForm.confirmPromptEvent = '';
		},
		confirmPromptNo: function(){
			$('#nwp-confirm-prompt-container').remove();
			$.fn.cProcessForm.confirmPromptEl = '';
			$.fn.cProcessForm.confirmPromptEvent = '';
			return false;
		},
		sendConfirmPrompt: function( prompt, $me, e ){
			if( prompt && ! $.fn.cProcessForm.confirmPromptEl ){
				$.fn.cProcessForm.confirmPromptEl = $me;
				$.fn.cProcessForm.confirmPromptEvent = e;
				$('body').prepend(`<div id="nwp-confirm-prompt-container">
						<div style="position: fixed; width: 100%;  height: 100%;  top: 0;   left: 0;  right: 0;  bottom: 0;  background-color: rgba(0,0,0,0.5);   z-index: 990000;   cursor: pointer;">
								<div style="position: absolute; top: 50%; left: 50%;	transform: translate(-50%,-50%); -ms-transform: translate(-50%,-50%); width: 400px; background: #fff;  padding: 50px; box-shadow: 2px 4px 2px #4e4e4e;">
										<big><strong>${prompt}</strong></big>
										<br /><br />
										<div class="row">
											<div class="col-md-6">
											<a class="btn-block btn btn-primary confirm-focus" href="#" onclick="$.fn.cProcessForm.confirmPromptYes();return false;">Yes &rarr;</a>
											</div>
											<div class="col-md-6">
												<a href="#" class="btn-block btn btn-secondary" onclick="$.fn.cProcessForm.confirmPromptNo();return false;">Cancel</a>
											</div>
										</div>
									</form>
							  </div>
							</div>
				</div>`);
				
				$("#nwp-confirm-prompt-container")
				.find(".confirm-focus")
				.focus();
			}
		},
		downloadCSVfromHTMLTable:function (id){
			//alert(id);
			var rows = [];
			$('#' + id)
			.find('table')
			.each(function(){
				if( ! $(this).hasClass('no-csv') ){
					var $cl2 = $(this).clone();
					$cl2.find(".no-csv").remove();
					
					$cl2.find('tr').each(function(){
						var cells = [];
						$(this).find('td,th').each(function(){
							var $cl = $(this).clone();
							$cl.find(".no-csv").remove();
							//console.log( $cl.html() );
							cells.push( $cl.text().trim() );
							//console.log( $(this).html() );
						});
						rows.push( cells );
					});
				}
			});
			
			var title = $('#' + id).parent().find('form.report-settings-form').find('input[name="report_title"]').val();
			if( ! title ){
				title = 'CSV File';
			}
			$.fn.cProcessForm.exportToCsv( title, rows );
		},
		exportToCsv: function(filename, rows) {
			var processRow = function (row) {
				var finalVal = '';
				for (var j = 0; j < row.length; j++) {
					var innerValue = row[j] === null ? '' : row[j].toString();
					if (row[j] instanceof Date) {
						innerValue = row[j].toLocaleString();
					};
					var result = innerValue.replace(/"/g, '""');
					if (result.search(/("|,|\n)/g) >= 0)
						result = '"' + result + '"';
					if (j > 0)
						finalVal += ',';
					finalVal += result;
				}
				return finalVal + '\n';
			};

			var csvFile = '';
			for (var i = 0; i < rows.length; i++) {
				csvFile += processRow(rows[i]);
			}

			var blob = new Blob([csvFile], { type: 'text/csv;charset=utf-8;' });
			if (navigator.msSaveBlob) { // IE 10+
				navigator.msSaveBlob(blob, filename);
			} else {
				var link = document.createElement("a");
				if (link.download !== undefined) { // feature detection
					// Browsers that support HTML5 download attribute
					var url = URL.createObjectURL(blob);
					link.setAttribute("href", url);
					link.setAttribute("download", filename);
					link.style.visibility = 'hidden';
					document.body.appendChild(link);
					link.click();
					document.body.removeChild(link);
				}
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


var nwTreeView = function() {
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
		if( $.fn.cProcessForm.nwp_hash ){
			params += '&nwp_hash=' + $.fn.cProcessForm.nwp_hash;
		}
		
		var surl = $.fn.cProcessForm.requestURL + 'php/ajax_request_processing_script.php?action='+action+'&todo='+todo + params;
		if( $.fn.cProcessForm.customURL ){
			surl = $.fn.cProcessForm.requestURL + '?action='+action+'&todo='+todo + params;
		}
		
		$( nwTreeView.selector )
		//.bind("refresh.jstree", function () {
		.bind("loaded.jstree", function () {
			if( nwTreeView.data && nwTreeView.data.click_first_item ){
				$( nwTreeView.selector )
				.find('ul > li > a:first')
				.click();
			}
			$.fn.cProcessForm.activateOpenNewTab(0);
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
					
					if( ids.length > 1 ){
						var ajax_data = { is_menu:1 };
						
						for( var x = 0; x < ids.length; x++ ){
							if( ids[ x ] ){
								var d2 = ids[ x ].split('=');
								if( d2.length > 1 ){
									ajax_data[ d2[0] ] = d2[1];
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
							ajax_data.is_menu = 1;
							
							$.fn.cProcessForm.ajax_data = {
								ajax_data: ajax_data,
								form_method: 'get',
								ajax_data_type: 'json',
								ajax_action: 'request_function_output',
								ajax_container: '',
								//ajax_get_url: "?action=" + $(this).attr("action") + "&todo=" + $(this).attr("todo"),
								ajax_get_url: "",
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

var nwBulkDataCapture = function () {
	return {
		cartItemsRemoved:{},
		autoCompleteIndex:{},
		autoCompleteCol2:{},
		autoCompleteCol:{},
		autoCompleteSelection:{},
		cartItems:{},
		unsaved:[],
		initialized:0,
		in_use:0,
		columns: {},
		colHeaders: {},
		tbcontainer: '',
		init: function () {
			//var c = ( global_data );
			//var c = JSON.parse( global_data );
			
		},
		hot:'',
		activateData:{},
		autocompleteSource: function (query, process) {
			
			var sel = nwBulkDataCapture.hot.getSelected();
			// && nwBulkDataCapture.cartItems[ sel[0] ] 
			//console.log( 's0x', nwBulkDataCapture.columns );
			//console.log( 's0x', sel[0][1] );
			//console.log( 's1', nwBulkDataCapture.cartItems[ sel[0][0] ] );
			var rd = [];
			
			//var action = '?action=customers&todo=get_customers_select2';
			var ccol = '';
			var action = '';
			var prop = '';
			if( typeof( sel[0] ) !== 'undefined' && typeof( sel[0][1] ) !== 'undefined' && nwBulkDataCapture.columns[ sel[0][1] ] && nwBulkDataCapture.columns[ sel[0][1] ].action ){
				ccol = sel[0][1];
				prop = nwBulkDataCapture.columns[ sel[0][1] ].data;
				action = nwBulkDataCapture.columns[ sel[0][1] ].action;
			}
			
			if( query.indexOf(" - ") >= 0 || query.indexOf(" [") >= 0 ){
				query = '';
				if( typeof( sel[0] ) !== 'undefined' && typeof( sel[0][0] ) !== 'undefined' ){
					//nwBulkDataCapture.hot.setDataAtRowProp( sel[0][0], prop, '', 'skip' );
					 
					//setTimeout( function(){
					//}, 3000 );
					nwBulkDataCapture.hot.setDataAtRowProp( sel[0][0], prop, '', 'skip' );
					return false;
					
					//	nwBulkDataCapture.hot.setDataAtRowProp( 1, 'account', 'qwerty', 'skip' );
				}
			}
			
			if( typeof( sel[0] ) !== 'undefined' && typeof( sel[0][0] ) !== 'undefined' ){
				//console.log( 's', nwBulkDataCapture.hot.getDataAtRow( sel[0][0] ) );
				rd = nwBulkDataCapture.hot.getDataAtRow( sel[0][0] );
			}
			
			var surl = $.fn.cProcessForm.requestURL +'php/ajax_request_processing_script.php' + action;
			if( $.fn.cProcessForm.customURL ){
				surl = $.fn.cProcessForm.requestURL + action;
			}
			if( $.fn.cProcessForm.nwp_hash ){
				surl += '&nwp_hash=' + $.fn.cProcessForm.nwp_hash;
			}
			
			$.ajax({
			  url: surl,
			  type:'post',
			  dataType: 'json',
			  data: {
				r_limit: 16,
				format: 'handsontable',
				row_data: JSON.stringify( rd ),
				term: query
			  },
			  success: function (response) {
				//console.log("response", response);
				var pr = [];
				if( response.items ){
					
					if( ! nwBulkDataCapture.autoCompleteIndex[ ccol ] ){
						nwBulkDataCapture.autoCompleteIndex[ ccol ] = {};
					}
					
					$.each( response.items, function( k, v ){
						if( v.text ){
							var t = '';
							if( v.serial_num ){
								t = v.text;
								//t = v.serial_num + ' - ' + v.text;
							}else{
								t = v.id + ' - ' + v.text;
							}
							//t = v.id + ':::::' + v.text;
							t = t.trim();
							pr.push( t );
							if( v.array ){
								nwBulkDataCapture.autoCompleteIndex[ ccol ][ md5( t ) ] = v;
							}else{
								nwBulkDataCapture.autoCompleteIndex[ ccol ][ md5( t ) ] = v.id;
							}
						}
					} );
				}
				//console.log( 'x', nwBulkDataCapture.autoCompleteIndex );
				//process(JSON.parse(response.data)); // JSON.parse takes string as a argument
				process( pr );
				//process([ "ben", "jason" ]);

			  }
			});
		},
		use_default_functions:1,
		activateHandsontable: function(){
			nwBulkDataCapture.cartItemsRemoved = {};
			nwBulkDataCapture.autoCompleteIndex = {};
			nwBulkDataCapture.autoCompleteCol2 = {};
			nwBulkDataCapture.autoCompleteCol = {};
			nwBulkDataCapture.autoCompleteSelection = {};
			nwBulkDataCapture.cartItems = {};
			nwBulkDataCapture.columns = {};
			nwBulkDataCapture.colHeaders = {};
			nwBulkDataCapture.use_default_functions = 1;
			
			setTimeout( nwBulkDataCapture.activateHandsontable2, 250 );
		},
		activateHandsontable2: function(){
			var p = nwBulkDataCapture.activateData;
			
			var colHeaders = [ 'ID', 'Sample' ];
			var columns = [
				{
				  data: 'id',
				  type: 'numeric',
				  width: 40
				},
				{
				data: 'currencyCode',
				type: 'autocomplete',
				source: nwBulkDataCapture.autocompleteSource,
				strict: true
				},
				
			];

			var data_object = {};

			var hotSettings = {
				// columns: columns,
				autoWrapRow: true,
				allowRemoveRow:false,
				allowInsertRow:false,
				allowInsertColumn:false,
				allowRemoveColumn:false,
				manualRowResize: true,
				manualColumnResize: true,
				rowHeaders: true,
				// colHeaders:colHeaders,
				manualRowMove: false,
				manualColumnMove: false,
				contextMenu: true,
				copyPaste: true,
				columnSorting: true,
				height: 420,
				//width: 806,
				stretchH: 'all',
				// colWidths: [ 1, 1, 200, 50, 100, 100, 100, 80, 70, 150 ],
				/* tableOverflow: true,
				tableHeight: "420px",
				tableWidth: $('#'+tbcontainer).css('width'), */
			};
			
			var tbcontainer;
			if( nwBulkDataCapture.tbcontainer ){
				tbcontainer = nwBulkDataCapture.tbcontainer;
			}
			
			if( p && p.hotSettings ){
				hotSettings = p.hotSettings;
			}

			if( nwBulkDataCapture.extraHotSettings && ! $.isEmptyObject(nwBulkDataCapture.extraHotSettings) ){
				$.each(nwBulkDataCapture.extraHotSettings, function(k,v){
					hotSettings[k] = v;
				});
			}

			hotSettings["contextMenu"] = [ 'undo', 'redo' ];
			var remove = 1;
			if( p && p.no_remove ){
				remove = 0;
			}
			if( remove && ! ( p && p.hotSettings ) ){
				hotSettings["allowRemoveRow"] = true;
				hotSettings["contextMenu"].push('remove_row');
			}
			if( remove && p && p.remove ){
				hotSettings["allowRemoveRow"] = true;
				hotSettings["contextMenu"].push('remove_row');
			}

			// console.log( p );
			if( typeof nwBulkDataCapture.auto_sel == 'object' && ! $.isEmptyObject( nwBulkDataCapture.auto_sel ) ){
				nwBulkDataCapture.autoCompleteSelection = nwBulkDataCapture.auto_sel;
				nwBulkDataCapture.auto_sel = {};
			}

			if( p && p.colHeaders && p.columns && p.tbcontainer ){
				//console.log( p );
				hotSettings.colHeaders = p.colHeaders;
				hotSettings.columns = p.columns;
				tbcontainer = p.tbcontainer;

				if( p.no_default_functions )nwBulkDataCapture.use_default_functions = 0;
				if( p.data )data_object = p.data;

			// }else if( typeof( global_columns ) !== 'undefined' ){
			}else if( typeof( nwBulkDataCapture.global_colHeaders ) !== 'undefined' && nwBulkDataCapture.global_colHeaders && typeof( nwBulkDataCapture.global_columns ) !== 'undefined' && typeof( nwBulkDataCapture.tbcontainer ) !== 'undefined' && nwBulkDataCapture.tbcontainer && nwBulkDataCapture.global_columns ){
				hotSettings.colHeaders = nwBulkDataCapture.global_colHeaders;
				hotSettings.columns = nwBulkDataCapture.global_columns;
				tbcontainer = nwBulkDataCapture.tbcontainer;
				
				if( typeof( nwBulkDataCapture.global_data ) !== 'undefined' && nwBulkDataCapture.global_data )data_object = nwBulkDataCapture.global_data;
			}else{
				alert( 'Invalid Handsontable Column Headers/Containers' );
			}
			
			
			//hotSettings.colHeaders = colHeaders;
			//hotSettings.columns = columns;
			$.each(hotSettings.columns, function( k1, v1 ){
				if( v1.type == 'autocomplete' ){
					nwBulkDataCapture.autoCompleteCol[ k1 ] = k1;
					nwBulkDataCapture.autoCompleteCol2[ v1.data ] = k1;
					hotSettings.columns[ k1 ].source = nwBulkDataCapture.autocompleteSource;
				}
			});
			nwBulkDataCapture.colHeaders = hotSettings.colHeaders;
			nwBulkDataCapture.columns = hotSettings.columns;
			nwBulkDataCapture.tbcontainer = tbcontainer;
			
			//console.log( nwBulkDataCapture.columns );
			//console.log( nwBulkDataCapture.colHeaders );
			//console.log( $('#'+tbcontainer).css('width') );
			//console.log( 'tc', tbcontainer );
			
			var add_data = 1;
			if( add_data ){
				hotSettings["data"] = data_object;
				//hotSettings["copyPaste"] = false;
			}
				
			// if( nwBulkDataCapture.in_use ){
				/*'row_abovex', 'row_belowx', */
				
			// }
			
			
			var hotElement = document.querySelector('#'+tbcontainer);
			//var hot = jspreadsheet( document.getElementById( tbcontainer ) , hotSettings);
			var hot = new Handsontable( hotElement , hotSettings);
			
			// var searchFiled = document.getElementById('search_field');
			
			//$('#'+tbcontainer).jexcel( hotSettings );
			
			hot.addHook('beforeRemoveRow', function( index, amount, physicalRows, source ){
				//console.log( index, amount );
				if( nwBulkDataCapture.update ){
					for( var i = index; i < ( index + amount ); i++ ){
						var dx = hot.getDataAtRow( i );
						
						if( dx[0] ){
							if( nwBulkDataCapture.use_default_functions ){
								nwBulkDataCapture.cartItemsRemoved[ dx[0] ] = 1;
								if( nwBulkDataCapture.cartItems[ dx[0] ] ){
									delete nwBulkDataCapture.cartItems[ dx[0] ];
									//console.log( dx );
								}
								if( nwBulkDataCapture.cartItems[ i ] ){
									delete nwBulkDataCapture.cartItems[ i ];
									//console.log( dx );
								}
								nwBulkDataCapture.afterChange2( { 'data' : dx, 'type' : 'delete' } );
							}else{
								nwBulkDataCapture.afterChange( { 'data' : dx, 'type' : 'delete' } );
							}
						}
						
					}
					if( nwBulkDataCapture.use_default_functions ){
						nwBulkDataCapture.refreshCart();
					}
				}
			});
			
			hot.addHook('afterChange', function(src, changes){
				
				//console.log( src, changes );
				if( nwBulkDataCapture.update && changes != 'skip' ){
					//console.log( 'a', nwBulkDataCapture.update );
					for( var i = 0; i < src.length; i++ ){
					//for( var ii = 0, ii < src[i].length, ii++ ){
						var row = src[i][0];
						var col = src[i][1];
						var val = src[i][3];
						
						var dx = hot.getDataAtRow( row );
						if( nwBulkDataCapture.use_default_functions ){
							nwBulkDataCapture.cartItems[ row ] = nwBulkDataCapture.afterChange2( { 'data' : dx, 'type' : 'edit', 'row':row, 'col':col } );
						}else{
							nwBulkDataCapture.afterChange( { 'data' : dx, 'type' : 'edit', 'row':row, 'col':col } );
						}
						//console.log( 'data-' + row, dx );
						//console.log( hot.getData() );
					}
					
					if( nwBulkDataCapture.use_default_functions ){
						nwBulkDataCapture.refreshCart();
					}
				}
			}); 
		   
			nwBulkDataCapture.hot = hot;
			
		  //hot.getData()
		},
		update: 1,
		afterChange: function( p ){
			return nwBulkDataCapture.afterChange2(p);
		},
		afterChange2: function( p ){
			//console.log(nwBulkDataCapture.columns);
			//console.log(p);
			if( p && p.data && p.type && p.type == 'edit' ){
				if( typeof( p.row ) !== 'undefined' && ! $.isEmptyObject( nwBulkDataCapture.autoCompleteCol ) ){
					$.each( nwBulkDataCapture.autoCompleteCol, function( k1, v1 ){
						if( p.data[ k1 ] && nwBulkDataCapture.autoCompleteIndex[ k1 ] && nwBulkDataCapture.autoCompleteIndex[ k1 ][ md5( p.data[ k1 ] ) ] ){
							if( ! nwBulkDataCapture.autoCompleteSelection[ p.row ] ){
								nwBulkDataCapture.autoCompleteSelection[ p.row ] = {};
							}
							nwBulkDataCapture.autoCompleteSelection[ p.row ][ k1 ] = nwBulkDataCapture.autoCompleteIndex[ k1 ][ md5( p.data[ k1 ] ) ];
						}
					});
				}
				/* if( typeof( p.row ) !== 'undefined' && typeof( p.col ) !== 'undefined' && ! $.isEmptyObject( nwBulkDataCapture.autoCompleteCol2 ) ){
					//console.log( p );
					//console.log( nwBulkDataCapture.autoCompleteCol2 );
					if( nwBulkDataCapture.autoCompleteCol2[ p.col ] ){
						var k1 = nwBulkDataCapture.autoCompleteCol2[ p.col ];
						if( p.data[ k1 ] ){
							var dx = p.data[ k1 ].split(":::::");
							if( dx[0] ){
								nwBulkDataCapture.hot.setDataAtRowProp( p.row, p.col + "_rid", dx[0], 'skip' );
							}
							//nwBulkDataCapture.hot.setDataAtRowProp( p.row, p.col, dx[1], 'skip' );
							p.data[ k1 - 1 ] = dx[0];
						}
					}
				} */
				return p.data;
			}
		},
		clearUnsavedData: function(){
			nwBulkDataCapture.update = 0;
			nwBulkDataCapture.cartItems = {};
			nwBulkDataCapture.cartItemsRemoved = {};
			nwBulkDataCapture.refreshCart();
			setTimeout(function(){ nwBulkDataCapture.update = 1; }, 800 );
		},
		clear: function(){
			nwBulkDataCapture.update = 0;
			nwBulkDataCapture.hot.clear();
			nwBulkDataCapture.cartItems = {};
			nwBulkDataCapture.cartItemsRemoved = {};
			nwBulkDataCapture.refreshCart();
			setTimeout(function(){ nwBulkDataCapture.update = 1; }, 800 );
		},
		autoPopuplateChanges: function(){
			setTimeout( function(){
				nwBulkDataCapture.cartItems = nwBulkDataCapture.hot.getData();
				nwBulkDataCapture.refreshCart();
			}, 350 );
		},
		getData: function(){
			return {
				columns: nwBulkDataCapture.columns,
				data: nwBulkDataCapture.cartItems,
				deleted: nwBulkDataCapture.cartItemsRemoved,
				auto_sel: nwBulkDataCapture.autoCompleteSelection,
			};
		},
		refreshCart: function(){
			var j = nwBulkDataCapture.getData();
			
			var count = 0;
			$.each( nwBulkDataCapture.cartItems, function( k, v ){
				++count;
			} );
			$.each( nwBulkDataCapture.cartItemsRemoved, function( k, v ){
				++count;
			} );
			
			if( $('#'+nwBulkDataCapture.tbcontainer+'-con') ){
				$('#'+nwBulkDataCapture.tbcontainer+'-con')
				.find(".the-notice-container")
				.text( count + ' unsaved changes' )
				.show();
			}
			
			if( $('form#'+nwBulkDataCapture.tbcontainer+'-form') ){
				$('form#'+nwBulkDataCapture.tbcontainer+'-form')
				.find('textarea[name="data"]')
				.val( JSON.stringify( j ) );
			}
		},
		addComma: function( nStr ){
			nStr = parseFloat( nStr ).toFixed(2);
			
			nStr += '';
			x = nStr.split('.');
			x1 = x[0];
			x2 = x.length > 1 ? '.' + x[1] : '';
			var rgx = /(\d+)(\d{3})/;
			while (rgx.test(x1)) {
				x1 = x1.replace(rgx, '$1' + ',' + '$2');
			}
			return x1 + x2;
		}
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

var nwConsentForms = {
	data:{},
	table:'',
	init:function(){
		setTimeout(function(){
			$( 'form[name="'+ nwConsentForms.table +'"]' ).find( 'input.embedded-field.select2' ).prev().attr( 'style', 'width:20% !important;' );
			$( 'form[name="'+ nwConsentForms.table +'"]' ).find( 'input.embedded-field.select2' ).prev().find( 'a' ).attr( 'style', 'border-left: 0px !important;border-top: 0px !important;border-right: 0px !important;' );
		}, 200);
	},
	createSignButton:function( table, field, replace, title ){
	    var html = '<a href="#" class="hidden-print no-print btn dark custom-single-selected-record-button sign-btnx" title="'+ title +'" override-selected-record="-" action="?field='+ field +'&type='+ replace +'&consent_type='+ table +'&action=nwp_other_invest_services&todo=execute&nwp_action=consent_form&nwp_todo=other_consent_signature&html_replacement_selector='+ replace +'-container&callback=nwConsentForms.saved_signature" data-id="sdvsdv">Click Here to Sign</a>'; 

	    html += '<div id="'+ replace +'-container" class="sign-con"></div>';
	    // $(this).html().replace("{"+ replace +"}", html ); 
	    $( 'form[name="'+ table +'"]' ).find( 'input[name="'+ field +'"]' ).attr( 'type', 'hidden' ); 

	    $( 'form[name="'+ table +'"]' ).find( '.'+ replace +'-btn-container' ).html( html ); 
	},
	saved_signature: function(){
		if( $.fn.cProcessForm.returned_ajax_data && $.fn.cProcessForm.returned_ajax_data.data && $.fn.cProcessForm.returned_ajax_data.data.path && $.fn.cProcessForm.returned_ajax_data.data.field && $.fn.cProcessForm.returned_ajax_data.data.consent_type ){
			var tb = $.fn.cProcessForm.returned_ajax_data.data.consent_type;
			var fl = $.fn.cProcessForm.returned_ajax_data.data.field;
			var path = $.fn.cProcessForm.returned_ajax_data.data.path;
		    $( 'form[name="'+ tb +'"]' ).find( 'input[name="'+ fl +'"]' ).val( path ); 
		    $( 'form[name="'+ tb +'"]' ).find( 'input[name="'+ fl +'"]' ).attr( 'value', path ); 
		}
	}
};


var nwClientForm = function () {
	return {
		data: {},
		x: function( $form_id ){
			alert(5);
		},
		getSavedData: function( $form_id ){
			var fstr = 'client-form-structure-'+$form_id;
			var json = JSON.parse( $( 'textarea#'+fstr ).val() );
			if( typeof json == 'object' && typeof json.values !== 'undefined' && ! $.isEmptyObject( json.values ) ){
				nwClientForm.data[ $form_id ] = json.values;
			}
		},
		submitForm: function( $form_id ){
			var fid = 'client-form-fields-'+$form_id;
			var btn = 'client_form_submit_'+$form_id;
			var fstr = 'client-form-structure-'+$form_id;

			var json = JSON.parse( $( 'textarea#'+fstr ).val() );

			$("input#"+btn)
			.parents('form')
			.on('submit', function(e){
				nwClientForm.data = {};
			});

			$("input#"+btn)
			.off('click')
			.on('click', function(e){
				e.preventDefault();
				
				var launch_date = new Date();
				var new_id = 't' + launch_date.getTime();
				var id = new_id;
				if( $( '#'+fid ).find( 'input[name="cf_id"]' ).val() ){
					id = $( '#'+fid ).find( 'input[name="cf_id"]' ).val();
					$( '#'+fid ).find( 'input[name="cf_id"]' ).val( '' )
				}
				
				var err = "";
				var msg = "";
				
				var data = {};
				var $d2 = {};

				$(this)
				.parents("div#"+fid)
				.find(".clientFormField")
				.each(function(){
					var val = $(this).val();
					var add = 1;
					var typ = $(this).attr("data-type");
					
					switch( typ ){
					case "hidden":
					case "text":
						if( $(this).hasClass("select2") ){
							var d = $(this).select2('data');
							if( ! $.isEmptyObject( d ) ){
								var n = $(this).attr("name");
								
								$.each( d, function( k, v ){
									if( k ){
										data[ n + "_" + k ] = v;
									}
								} );
							}
						}
						if( $(this).hasClass('auto-num-active') ){
							val = val.replaceAll(',', '');
							val = parseFloat( val );
							if( isNaN( val ) )val = 0;
						}
					break;
					case "number":
						if( $(this).hasClass('auto-num-active') ){
							val = val.replaceAll(',', '');
						}
						val = parseFloat( val );
						if( isNaN( val ) )val = 0;
					break;
					case "json":
						val = JSON.parse( val );
						if( typeof( val ) !== 'object' ){
							val = {};
							add = 0;
						}
					break;
					case "select":
						var n = $(this).attr("name");
						
						if( $(this).val() ){
							var val = $(this).val().toString();
						}else{
							val = '';
						}
						
						data[ n + "_text" ] = $(this).find('option:selected').text().trim();
						
					break;
					}
					
					if( $(this).hasClass("skip-if-empty") ){
						if( ! val ){
							add = 0;
						}
					}
					
					if( add ){
						data[ $(this).attr("name") ] = val;
					}
				});
				
				data["id"] = id;
				if( typeof nwClientForm.data[ $form_id ] == 'undefined' ){
					nwClientForm.data[ $form_id ] = {};
				}
				nwClientForm.data[ $form_id ][ id ] = data;
				// console.log( data );
				
				if( err ){
					var data = {theme:'alert-danger', err:err, msg:msg, typ:'jsuerror' };
					nwDisplayNotification.display_notification( data );
					return false;
				}else{
					nwClientForm.refreshClientForm( $form_id );
				}
				
				$(this).find('input').not('.retain-value').val("");
				$(this).find('textarea').not('.retain-value').val("");
				
				if( $(this).find("input.select2") ){
					$(this).find("input.select2").not('.retain-value').select2("val", "");
				}
				if( $(this).find("input.uploaded-file") ){
					$(this).find("input.uploaded-file").val("");
					$(this).find(".qq-upload-list").html("");
				}
			});

		},
		refreshClientForm: function( $form_id ){
			var fstr = 'client-form-structure-'+$form_id;
			var fdata = 'client-form-data-'+$form_id;
			var json = JSON.parse( $( 'textarea#'+fstr ).val() );
			console.log( json )

			var html = '';
			if( typeof json == 'object' && typeof json.fields !== 'undefined' && ! $.isEmptyObject( json.fields ) ){
				if( ! $.isEmptyObject( nwClientForm.data ) && ! $.isEmptyObject( nwClientForm.data[ $form_id ] )  ){
					var total = {};
					$.each( nwClientForm.data[ $form_id ], function( k, v ){
						html += '<tr>';
						$.each( json.fields, function( k1, v1 ){
							if( typeof v1.id != 'undefined' ){
								var k2 = v1.id + '_text';
								if( typeof v[ k2 ] != 'undefined' ){
									html += '<td>'+ v[ k2 ] +'</td>';
								}else if( typeof v[ v1.id ] != 'undefined' ){
									html += '<td>'+ v[ v1.id ] +'</td>';
								}else{
									html += '<td>&nbsp;</td>';
								}

								if( typeof total[ ' '+v1.id ] == 'undefined' ){
									total[ ' '+v1.id ] = 0;
								}
								switch( v1.form_field ){
								case 'number':
									if( typeof v[ v1.id ] != 'undefined' ){
										total[ ' '+v1.id ] += parseFloat( v[ v1.id ] );
									}
								break;
								default:
									total[ ' '+v1.id ] = '';
								break;
								}
							}
						});

						html += ' <td class="r"> ';

						html += ' <a onclick="nwClientForm.delete('+ "'"+ k +"','"+ $form_id +"'" +');" title="Remove this Record" class="btn btn-sm default"> <i class="icon-trash"></i> </a>';

						html += ' <a onclick="nwClientForm.edit('+ "'"+ k +"','"+ $form_id +"'" +');" title="Edit this Record" class="btn btn-sm default"> <i class="icon-edit"></i> </a>';
						
						html += '</td>';

						html += '</tr>';

					});

					if( typeof json.total != 'undefined' && json.total ){
						html += '<tr><td colspan="'+ ( Object.getOwnPropertyNames( total ).length + 1 ) +'">&nbsp</td></tr>';
						html += '<tr>';
						$.each( total, function( a, b ){
							html += '<td><strong>'+ b +'</strong></td>';
						});
						html += ' <td class="r"><h4><strong>TOTAL</strong></h4></td>';
						html += '</tr>';
					}

					$( 'textarea#'+fdata ).val( JSON.stringify( nwClientForm.data[ $form_id ] ) );
				}
			}
			$( 'tbody#client-table-view-'+$form_id ).html( html );

		},
		edit: function( id, $form_id ){
			var fid = 'client-form-fields-'+$form_id;

			if( nwClientForm.data[ $form_id ] && nwClientForm.data[ $form_id ][ id ] && ! $.isEmptyObject( nwClientForm.data[ $form_id ][ id ] ) ){
				var fields = nwClientForm.data[ $form_id ][ id ];

				$.each( fields, function( k, v ){
					switch( k ){
					case 'forms':
					break;
					default:
						$( '#'+fid ).find( 'input[name="'+ k +'"],select[name="'+ k +'"],textarea[name="'+ k +'"]' ).val( v );
					break;
					}
				});

				$( '#'+fid ).find( 'input[name="cf_id"]' ).val( id );
			}

		},
		delete: function( id, $form_id ){
			var fid = 'client-form-fields-'+$form_id;

			if( nwClientForm.data[ $form_id ] && nwClientForm.data[ $form_id ][ id ] && ! $.isEmptyObject( nwClientForm.data[ $form_id ][ id ] ) ){
				delete nwClientForm.data[ $form_id ][ id ];
			}
			nwClientForm.refreshClientForm( $form_id );
			
		},
	};
}();

window.addEventListener('popstate', function(event) {
	// alert(5);
	$.fn.cProcessForm.userHistory( { 'type' : 'use', 'event' : event } );
	// history.pushState(null, null, window.location.pathname);

}, false);

// 23rd October, 2023 - Mike
$.fn.serializeAndEncode = function() {
    var $form = this; // reference to the form
    return $.map(this.serializeArray(), function(val) {
        var $input = $form.find("[name='" + val.name + "']"); // find the input field within the form
        if ($input.hasClass('auto-num-active')) {
            val.value = val.value.replace(/,/g, '');
        }
        return [val.name, encodeURIComponent(val.value)].join('=');
    }).join('&');
};

$.fn.serializeAndEncodeOld = function() {
    return $.map(this.serializeArray(), function(val) {
        return [val.name, encodeURIComponent(val.value)].join('=');
    }).join('&');
};
