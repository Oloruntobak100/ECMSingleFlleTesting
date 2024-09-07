(function($) {
    $.fn.nwLogin = {
        checkAuthenticationStatus: function() {
            //check for get params
            var dS = {};
            if (document.location.search){
                //console.log("d", document.location.search);
                var cl = document.location.search.replace('?', '');
                if (cl) {
                    var cl2 = cl.split('&');
                    for (var i = 0; i < cl2.length; i++) {
                        var cl3 = cl2[i].split('=');
                        if (cl3[0] && cl3[1]) {
                            dS[ cl3[0] ] = cl3[1];
                        }
                    }
                }
            }
            dS.filter = "app";

			$.fn.cProcessForm.ajax_data = {
                ajax_data: dS,
                form_method: 'post',
                ajax_data_type: 'json',
                ajax_action: 'request_function_output',
                ajax_container: '',
				ajax_get_url: "?action=endpoint&todo=app_check_logged_in_user2",
            };
            $.fn.cProcessForm.ajax_send();
			
        },
    }
}(jQuery));